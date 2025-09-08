#!/usr/bin/env python3
import argparse
import email
import os
import sys
from datetime import datetime, timedelta, date
from email.header import decode_header, make_header
from imapclient import IMAPClient
import ssl


def decode_maybe(value):
    if value is None:
        return None
    try:
        return str(make_header(decode_header(value)))
    except Exception:
        return value


def format_imap_date(dt: datetime) -> str:
    # IMAP date: 05-Sep-2025
    return dt.strftime('%d-%b-%Y')


def parse_args():
    parser = argparse.ArgumentParser(description='Download mail attachments by subject, sender, and date range')
    parser.add_argument('--host', required=True)
    parser.add_argument('--port', type=int, default=993)
    parser.add_argument('--username', required=True)
    parser.add_argument('--password', required=True)
    parser.add_argument('--subject', default=None, help='subject contains, case-insensitive')
    parser.add_argument('--from_email', default=None, help='filter by sender email')
    parser.add_argument('--date', default=None, help='single date yyyy-MM-dd')
    parser.add_argument('--date_start', default=None, help='start date yyyy-MM-dd')
    parser.add_argument('--date_end', default=None, help='end date yyyy-MM-dd')
    parser.add_argument('--dest', required=True, help='destination directory')
    return parser.parse_args()


def login_imap(host, port, username, password):
    try:
        client = IMAPClient(host, port=port, ssl=True)
        client.login(username, password)
        return client
    except Exception as e:
        if 'CERTIFICATE_VERIFY_FAILED' in str(e):
            print('[SSL_WARN] certificate verify failed; retry with insecure context', file=sys.stderr)
            ctx = ssl.create_default_context()
            ctx.check_hostname = False
            ctx.verify_mode = ssl.CERT_NONE
            client = IMAPClient(host, port=port, ssl=True, ssl_context=ctx)
            client.login(username, password)
            return client
        raise


def ensure_dir(path):
    os.makedirs(path, exist_ok=True)


def build_search_criteria(subject, from_email, date_single, date_start, date_end):
    crit = ['ALL']
    def parse_date_only(s):
        return datetime.strptime(s, '%Y-%m-%d').date()

    # 日期范围
    if date_single:
        d = parse_date_only(date_single)
        crit.extend(['SINCE', d, 'BEFORE', d + timedelta(days=1)])
    else:
        if date_start:
            ds = parse_date_only(date_start)
            crit.extend(['SINCE', ds])
        if date_end:
            de = parse_date_only(date_end)
            crit.extend(['BEFORE', de + timedelta(days=1)])

    # 发件人
    if from_email:
        crit.extend(['FROM', from_email])

    # 主题
    if subject:
        # 服务端搜索尽量用 HEADER Subject（比 SUBJECT 更稳定）
        crit.extend(['HEADER', 'Subject', subject])

    return crit


def message_matches_subject(msg, subject):
    if not subject:
        return True
    subj = decode_maybe(msg.get('Subject')) or ''
    return subject.lower() in subj.lower()


def save_attachments(msg, dest_dir):
    saved = 0
    for part in msg.walk():
        if part.get_content_maintype() == 'multipart':
            continue
        filename = part.get_filename()
        if not filename:
            disp = part.get('Content-Disposition') or ''
            if 'attachment' not in disp.lower():
                continue
            filename = 'attachment.bin'
        filename = str(make_header(decode_header(filename)))
        payload = part.get_payload(decode=True)
        if payload is None:
            continue
        path = os.path.join(dest_dir, filename)
        try:
            with open(path, 'wb') as f:
                f.write(payload)
            saved += 1
            print(f'[SAVE] {path}')
        except Exception as e:
            print(f'[ERROR] save {filename}: {e}', file=sys.stderr)
    return saved


def main():
    args = parse_args()
    ensure_dir(args.dest)

    try:
        client = login_imap(args.host, args.port, args.username, args.password)
    except Exception as e:
        print(f'[LOGIN_ERROR] {e}', file=sys.stderr)
        sys.exit(2)

    try:
        client.select_folder('INBOX', readonly=True)

        # 构建搜索条件
        criteria = build_search_criteria(args.subject, args.from_email, args.date, args.date_start, args.date_end)
        try:
            ids = client.search(criteria)
        except Exception as e:
            print(f'[SEARCH_WARN] {e}; fallback to ALL', file=sys.stderr)
            ids = client.search(['ALL'])

        # 新到旧遍历
        ids = list(sorted(ids, reverse=True))

        total_saved = 0
        preview_count = 0
        for num in ids:
            fetched = client.fetch([num], ['RFC822'])
            data = fetched.get(num)
            if not data or b'RFC822' not in data:
                continue
            raw = data[b'RFC822']
            msg = email.message_from_bytes(raw)

            # 客户端兜底：主题包含关键字（中文/大小写）
            if not message_matches_subject(msg, args.subject):
                if preview_count < 10:
                    print('[SKIP] subject=', decode_maybe(msg.get('Subject')))
                    preview_count += 1
                continue

            saved = save_attachments(msg, args.dest)
            total_saved += saved

        print(f'[DONE] attachments_saved={total_saved}')
    finally:
        try:
            client.logout()
        except Exception:
            pass


if __name__ == '__main__':
    main()
