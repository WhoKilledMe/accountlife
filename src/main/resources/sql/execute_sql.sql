truncate table fin_statement;
truncate table fin_statement_file;
select * from fin_statement_file;
select * from fin_statement;
select * from fin_statement_account_map;
select * from fin_payment_method;
select * from fin_transaction;

select * from fin_payment_method;
select * from fin_account;

select * from transaction_category;

SELECT * FROM category_keyword_mapping
WHERE is_active = true
  AND (
    keyword LIKE CONCAT('%', :keyword, '%')
        OR :keyword LIKE CONCAT('%', keyword, '%')
    )
ORDER BY weight DESC, keyword ASC;

select a.*, b.name
from fin_statement a
         left join transaction_category b on a.category_id = b.id
order by a.platform_code;

select * from fin_account;

select * from fin_account;
select * from fin_payment_method;
select * from fin_statement_mapping_rule;
select * from transaction_category;
select * from category_keyword_mapping where category_id = 2105 ;