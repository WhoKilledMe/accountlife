package com.acco.life.service;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Decoder {

    private static final Map<Character, String> ABC_MAP = Map.of(
            'A', "0",
            'B', "1",
            'C', "2",
            'D', "3",
            'E', "4"
    );

    private static final Map<Character, String> TF_MAP = Map.of(
            'T', "0",
            'F', "1"
    );

    public static void main(String[] args) throws Exception {
        String input = "CAFFFTTTFDBABBBACCDD CD BCD BDE F T F F";

        String[] answers = decodeByQuestion(input);
        ObjectMapper mapper = new ObjectMapper();
// 1️⃣ 允许 JSON 中存在实体类没有的字段
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String json =  str();

        // 1. 反序列化

        List<Paper> papers = mapper.readValue(
                json,
                mapper.getTypeFactory()
                        .constructCollectionType(List.class, Paper.class)
        );

        // 2. 填充答案
        fillAnswers(papers.get(0), answers);

        // 3. 序列化回 JSON
        String result = mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(papers);

        System.out.println(result);
    }

    /**
     * 核心方法：每一题 = 数组一个元素
     */
    public static String[] decodeByQuestion(String input) {
        List<String> result = new ArrayList<>();

        String[] segments = input.split(" ");

        int i = 0;
        for (String segment : segments) {

            // 多选题：连续 ABCDE，整体作为一题
            if (segment.matches("[ABCDE]+") && segment.length() > 1 && i > 0) {
                List<String> options = new ArrayList<>();
                for (char c : segment.toCharArray()) {
                    options.add(ABC_MAP.get(c));
                }
                result.add(String.join(",", options));
                continue;
            }
            i = i + 1;
            // 单选 / 判断题：一个字符 = 一题
            for (char c : segment.toCharArray()) {
                if (ABC_MAP.containsKey(c)) {
                    result.add(ABC_MAP.get(c));
                } else if (TF_MAP.containsKey(c)) {
                    result.add(TF_MAP.get(c));
                } else {
                    throw new IllegalArgumentException("非法字符: " + c);
                }
            }
        }

        return result.toArray(new String[0]);
    }

    public static void fillAnswers(Paper paper, String[] answers) {
        List<Question> questions = paper.getChildList();

        if (questions.size() != answers.length) {
            throw new IllegalArgumentException(
                    "题目数量(" + questions.size() +
                            ") 与答案数量(" + answers.length + ")不一致"
            );
        }

        for (int i = 0; i < questions.size(); i++) {
            questions.get(i).setAnswer(answers[i]);
        }
    }
    public static String str(){
        return """
                 [
                            {
                                "childList": [
                                    {
                                        "questionTitleAttachments": [],
                                        "attachments": [],
                                        "comments": "",
                                        "questionTitle": "1、当目标顾客人数众多时，生产商倾向于利用__________。",
                                        "questionDiffCode": "2",
                                        "isAnswerCorrect": false,
                                        "studentItemScore": 0,
                                        "commentsAttachments": [],
                                        "optionList": [
                                            {
                                                "attachments": [],
                                                "content": "长而宽的渠道"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "短渠道"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "窄渠道"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "直接渠道"
                                            }
                                        ],
                                        "objectiveAnswer": "",
                                        "subjectiveAnswer": "",
                                        "itemScore": 4.8,
                                        "questionTypeName": "单选题",
                                        "questionDiffName": "中",
                                        "questionTypeCode": "single",
                                        "id": "2c9081729b834dff019b8931a9150836"
                                    },
                                    {
                                        "questionTitleAttachments": [],
                                        "attachments": [],
                                        "comments": "",
                                        "questionTitle": "2、从经济价值链的系统看，营销中介的任务是________。",
                                        "questionDiffCode": "2",
                                        "isAnswerCorrect": false,
                                        "studentItemScore": 0,
                                        "commentsAttachments": [],
                                        "optionList": [
                                            {
                                                "attachments": [],
                                                "content": "把半成品变成成品"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "把消费者需求转换成生产上需要"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "把消费者需要和欲望转换成产品需要"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "把生产商生产出来的产品更高效率地送达消费者手中"
                                            }
                                        ],
                                        "objectiveAnswer": "",
                                        "subjectiveAnswer": "",
                                        "itemScore": 3.4,
                                        "questionTypeName": "单选题",
                                        "questionDiffName": "中",
                                        "questionTypeCode": "single",
                                        "id": "2c9081729b834dff019b8931a9160837"
                                    },
                                    {
                                        "questionTitleAttachments": [],
                                        "attachments": [],
                                        "comments": "",
                                        "questionTitle": "3、何种原因促使厂商不使用中间商？",
                                        "questionDiffCode": "2",
                                        "isAnswerCorrect": false,
                                        "studentItemScore": 0,
                                        "commentsAttachments": [],
                                        "optionList": [
                                            {
                                                "attachments": [],
                                                "content": "在目标市场中厂家直接销售商品的效率更高"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "间接销售降低了厂家的利润"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "避免中间商和顾客接触"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "专业化"
                                            }
                                        ],
                                        "objectiveAnswer": "",
                                        "subjectiveAnswer": "",
                                        "itemScore": 3.4,
                                        "questionTypeName": "单选题",
                                        "questionDiffName": "中",
                                        "questionTypeCode": "single",
                                        "id": "2c9081729b834dff019b8931a9160838"
                                    },
                                    {
                                        "questionTitleAttachments": [],
                                        "attachments": [],
                                        "comments": "",
                                        "questionTitle": "4、在渠道中，中间商层次的多少被称为渠道的________。",
                                        "questionDiffCode": "2",
                                        "isAnswerCorrect": false,
                                        "studentItemScore": 0,
                                        "commentsAttachments": [],
                                        "optionList": [
                                            {
                                                "attachments": [],
                                                "content": "长度"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "深度"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "宽度"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "相似性"
                                            }
                                        ],
                                        "objectiveAnswer": "",
                                        "subjectiveAnswer": "",
                                        "itemScore": 3.4,
                                        "questionTypeName": "单选题",
                                        "questionDiffName": "中",
                                        "questionTypeCode": "single",
                                        "id": "2c9081729b834dff019b8931a9170839"
                                    },
                                    {
                                        "questionTitleAttachments": [],
                                        "attachments": [],
                                        "comments": "",
                                        "questionTitle": "5、Fingerhut 和land,s End公司通过邮购、电话或网络销售产品，这属于下列哪种类型的渠道？",
                                        "questionDiffCode": "2",
                                        "isAnswerCorrect": false,
                                        "studentItemScore": 0,
                                        "commentsAttachments": [],
                                        "optionList": [
                                            {
                                                "attachments": [],
                                                "content": "间接营销渠道"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "向前渠道"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "直接营销渠道"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "混合渠道"
                                            }
                                        ],
                                        "objectiveAnswer": "",
                                        "subjectiveAnswer": "",
                                        "itemScore": 3.4,
                                        "questionTypeName": "单选题",
                                        "questionDiffName": "中",
                                        "questionTypeCode": "single",
                                        "id": "2c9081729b834dff019b8931a918083a"
                                    },
                                    {
                                        "questionTitleAttachments": [],
                                        "attachments": [],
                                        "comments": "",
                                        "questionTitle": "1、下列哪一项不属于促销组合的工具？",
                                        "questionDiffCode": "2",
                                        "isAnswerCorrect": false,
                                        "studentItemScore": 0,
                                        "commentsAttachments": [],
                                        "optionList": [
                                            {
                                                "attachments": [],
                                                "content": "人员推销"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "销售推广"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "组织计划"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "广告"
                                            }
                                        ],
                                        "objectiveAnswer": "",
                                        "subjectiveAnswer": "",
                                        "itemScore": 3.4,
                                        "questionTypeName": "单选题",
                                        "questionDiffName": "中",
                                        "questionTypeCode": "single",
                                        "id": "2c9081729b834dff019b8931a918083b"
                                    },
                                    {
                                        "questionTitleAttachments": [],
                                        "attachments": [],
                                        "comments": "",
                                        "questionTitle": "2、如果一个企业试图通过有利的宣传建立一个良好的公司形象，他们将会采用_______沟通工具。",
                                        "questionDiffCode": "2",
                                        "isAnswerCorrect": false,
                                        "studentItemScore": 0,
                                        "commentsAttachments": [],
                                        "optionList": [
                                            {
                                                "attachments": [],
                                                "content": "广告"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "公共关系"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "直销"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "销售推广"
                                            }
                                        ],
                                        "objectiveAnswer": "",
                                        "subjectiveAnswer": "",
                                        "itemScore": 3.4,
                                        "questionTypeName": "单选题",
                                        "questionDiffName": "中",
                                        "questionTypeCode": "single",
                                        "id": "2c9081729b834dff019b8931a919083c"
                                    },
                                    {
                                        "questionTitleAttachments": [],
                                        "attachments": [],
                                        "comments": "",
                                        "questionTitle": "3、揭示一种产品的新用途、更正错误的印象、减少购买者的顾虑，这些都能通过_______实现。",
                                        "questionDiffCode": "2",
                                        "isAnswerCorrect": false,
                                        "studentItemScore": 0,
                                        "commentsAttachments": [],
                                        "optionList": [
                                            {
                                                "attachments": [],
                                                "content": "需求导向广告"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "劝说性广告"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "提醒性广告"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "告知性广告"
                                            }
                                        ],
                                        "objectiveAnswer": "",
                                        "subjectiveAnswer": "",
                                        "itemScore": 3.4,
                                        "questionTypeName": "单选题",
                                        "questionDiffName": "中",
                                        "questionTypeCode": "single",
                                        "id": "2c9081729b834dff019b8931a919083d"
                                    },
                                    {
                                        "questionTitleAttachments": [],
                                        "attachments": [],
                                        "comments": "",
                                        "questionTitle": "4、啤酒、软饮料和洗衣粉都是无差异化产品，即与同一类别中的其他品牌极为相似。以下哪一种广告投放方式适合于这一类产品？",
                                        "questionDiffCode": "2",
                                        "isAnswerCorrect": false,
                                        "studentItemScore": 0,
                                        "commentsAttachments": [],
                                        "optionList": [
                                            {
                                                "attachments": [],
                                                "content": "大力度投放广告，让自己与众不同"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "中度投放广告，留存预算以备将来竞争更激烈时使用"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "轻度投放广告，因为消费者已经对所有竞争品牌很熟悉，而且已建立一定的品牌忠诚度"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "不投放广告，因为根本没用"
                                            }
                                        ],
                                        "objectiveAnswer": "",
                                        "subjectiveAnswer": "",
                                        "itemScore": 3.4,
                                        "questionTypeName": "单选题",
                                        "questionDiffName": "中",
                                        "questionTypeCode": "single",
                                        "id": "2c9081729b834dff019b8931a91a083e"
                                    },
                                    {
                                        "questionTitleAttachments": [],
                                        "attachments": [],
                                        "comments": "",
                                        "questionTitle": "5、下列哪种销售推广较多使用赠送样品、赠送赠券和折扣等形式？",
                                        "questionDiffCode": "2",
                                        "isAnswerCorrect": false,
                                        "studentItemScore": 0,
                                        "commentsAttachments": [],
                                        "optionList": [
                                            {
                                                "attachments": [],
                                                "content": "消费者推广"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "经销商推广"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "销售人员推广"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "渠道推广"
                                            }
                                        ],
                                        "objectiveAnswer": "",
                                        "subjectiveAnswer": "",
                                        "itemScore": 3.4,
                                        "questionTypeName": "单选题",
                                        "questionDiffName": "中",
                                        "questionTypeCode": "single",
                                        "id": "2c9081729b834dff019b8931a91a083f"
                                    },
                                    {
                                        "questionTitleAttachments": [],
                                        "attachments": [],
                                        "comments": "",
                                        "questionTitle": "2、研究表明，改变态度相对比较容易，而改变信念则比较困难。",
                                        "questionDiffCode": "2",
                                        "isAnswerCorrect": false,
                                        "studentItemScore": 0,
                                        "commentsAttachments": [],
                                        "optionList": [
                                            {
                                                "attachments": [],
                                                "content": "正确"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "错误"
                                            }
                                        ],
                                        "objectiveAnswer": "",
                                        "subjectiveAnswer": "",
                                        "itemScore": 3.4,
                                        "questionTypeName": "判断题",
                                        "questionDiffName": "中",
                                        "questionTypeCode": "judgment",
                                        "id": "2c9081729b834dff019b8931a91b0840"
                                    },
                                    {
                                        "questionTitleAttachments": [],
                                        "attachments": [],
                                        "comments": "",
                                        "questionTitle": "3、不同亚文化群的消费者有相同的生活方式。",
                                        "questionDiffCode": "2",
                                        "isAnswerCorrect": false,
                                        "studentItemScore": 0,
                                        "commentsAttachments": [],
                                        "optionList": [
                                            {
                                                "attachments": [],
                                                "content": "正确"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "错误"
                                            }
                                        ],
                                        "objectiveAnswer": "",
                                        "subjectiveAnswer": "",
                                        "itemScore": 3.4,
                                        "questionTypeName": "判断题",
                                        "questionDiffName": "中",
                                        "questionTypeCode": "judgment",
                                        "id": "2c9081729b834dff019b8931a91c0841"
                                    },
                                    {
                                        "questionTitleAttachments": [],
                                        "attachments": [],
                                        "comments": "",
                                        "questionTitle": "4、即使企业不支出任何营销费用，市场对某种产品仍然存在一个基本的需求量。",
                                        "questionDiffCode": "2",
                                        "isAnswerCorrect": false,
                                        "studentItemScore": 0,
                                        "commentsAttachments": [],
                                        "optionList": [
                                            {
                                                "attachments": [],
                                                "content": "正确"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "错误"
                                            }
                                        ],
                                        "objectiveAnswer": "",
                                        "subjectiveAnswer": "",
                                        "itemScore": 3.4,
                                        "questionTypeName": "判断题",
                                        "questionDiffName": "中",
                                        "questionTypeCode": "judgment",
                                        "id": "2c9081729b834dff019b8931a91c0842"
                                    },
                                    {
                                        "questionTitleAttachments": [],
                                        "attachments": [],
                                        "comments": "",
                                        "questionTitle": "1、美国的左撇子人口大约有3250万，但大多数公司并没有试图吸引或为这个群体设计产品，因为关于这个群体的统计数据很少。所以不符合有效市场细分中的真实性条件。",
                                        "questionDiffCode": "2",
                                        "isAnswerCorrect": false,
                                        "studentItemScore": 0,
                                        "commentsAttachments": [],
                                        "optionList": [
                                            {
                                                "attachments": [],
                                                "content": "正确"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "错误"
                                            }
                                        ],
                                        "objectiveAnswer": "",
                                        "subjectiveAnswer": "",
                                        "itemScore": 3.4,
                                        "questionTypeName": "判断题",
                                        "questionDiffName": "中",
                                        "questionTypeCode": "judgment",
                                        "id": "2c9081729b834dff019b8931a91d0843"
                                    },
                                    {
                                        "questionTitleAttachments": [],
                                        "attachments": [],
                                        "comments": "",
                                        "questionTitle": "2、橙汁生产商知道在早晨的消费量最大，但他们却试图改变人们的这个习惯，使橙汁在一天的其他时间段里同样受欢迎。他们需要采取时机细分模式并建立能够实施实现他们愿望的战略。",
                                        "questionDiffCode": "2",
                                        "isAnswerCorrect": false,
                                        "studentItemScore": 0,
                                        "commentsAttachments": [],
                                        "optionList": [
                                            {
                                                "attachments": [],
                                                "content": "正确"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "错误"
                                            }
                                        ],
                                        "objectiveAnswer": "",
                                        "subjectiveAnswer": "",
                                        "itemScore": 3.4,
                                        "questionTypeName": "判断题",
                                        "questionDiffName": "中",
                                        "questionTypeCode": "judgment",
                                        "id": "2c9081729b834dff019b8931a91d0844"
                                    },
                                    {
                                        "questionTitleAttachments": [],
                                        "attachments": [],
                                        "comments": "",
                                        "questionTitle": "3、“反市场细分”就是反对市场细分。",
                                        "questionDiffCode": "2",
                                        "isAnswerCorrect": false,
                                        "studentItemScore": 0,
                                        "commentsAttachments": [],
                                        "optionList": [
                                            {
                                                "attachments": [],
                                                "content": "正确"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "错误"
                                            }
                                        ],
                                        "objectiveAnswer": "",
                                        "subjectiveAnswer": "",
                                        "itemScore": 3.4,
                                        "questionTypeName": "判断题",
                                        "questionDiffName": "中",
                                        "questionTypeCode": "judgment",
                                        "id": "2c9081729b834dff019b8931a91e0845"
                                    },
                                    {
                                        "questionTitleAttachments": [],
                                        "attachments": [],
                                        "comments": "",
                                        "questionTitle": "4、市场细分后分出的每一个细分市场对企业的市场营销都具有重要的意义。",
                                        "questionDiffCode": "2",
                                        "isAnswerCorrect": false,
                                        "studentItemScore": 0,
                                        "commentsAttachments": [],
                                        "optionList": [
                                            {
                                                "attachments": [],
                                                "content": "正确"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "错误"
                                            }
                                        ],
                                        "objectiveAnswer": "",
                                        "subjectiveAnswer": "",
                                        "itemScore": 3.4,
                                        "questionTypeName": "判断题",
                                        "questionDiffName": "中",
                                        "questionTypeCode": "judgment",
                                        "id": "2c9081729b834dff019b8931a91f0846"
                                    },
                                    {
                                        "questionTitleAttachments": [],
                                        "attachments": [],
                                        "comments": "",
                                        "questionTitle": "1、好的品牌名称能够暗示产品利益和质量方面的信息。",
                                        "questionDiffCode": "2",
                                        "isAnswerCorrect": false,
                                        "studentItemScore": 0,
                                        "commentsAttachments": [],
                                        "optionList": [
                                            {
                                                "attachments": [],
                                                "content": "正确"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "错误"
                                            }
                                        ],
                                        "objectiveAnswer": "",
                                        "subjectiveAnswer": "",
                                        "itemScore": 3.4,
                                        "questionTypeName": "判断题",
                                        "questionDiffName": "中",
                                        "questionTypeCode": "judgment",
                                        "id": "2c9081729b834dff019b8931a91f0847"
                                    },
                                    {
                                        "questionTitleAttachments": [],
                                        "attachments": [],
                                        "comments": "",
                                        "questionTitle": "2、企业采用服务差异化的市场定位战略，就可以不再追求技术和质量的提高。",
                                        "questionDiffCode": "2",
                                        "isAnswerCorrect": false,
                                        "studentItemScore": 0,
                                        "commentsAttachments": [],
                                        "optionList": [
                                            {
                                                "attachments": [],
                                                "content": "正确"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "错误"
                                            }
                                        ],
                                        "objectiveAnswer": "",
                                        "subjectiveAnswer": "",
                                        "itemScore": 3.4,
                                        "questionTypeName": "判断题",
                                        "questionDiffName": "中",
                                        "questionTypeCode": "judgment",
                                        "id": "2c9081729b834dff019b8931a91f0848"
                                    },
                                    {
                                        "questionTitleAttachments": [],
                                        "attachments": [],
                                        "comments": "",
                                        "questionTitle": "3、驰名商标的知名度和影响力关键取决于国家商标局的认可。",
                                        "questionDiffCode": "2",
                                        "isAnswerCorrect": false,
                                        "studentItemScore": 0,
                                        "commentsAttachments": [],
                                        "optionList": [
                                            {
                                                "attachments": [],
                                                "content": "正确"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "错误"
                                            }
                                        ],
                                        "objectiveAnswer": "",
                                        "subjectiveAnswer": "",
                                        "itemScore": 3.4,
                                        "questionTypeName": "判断题",
                                        "questionDiffName": "中",
                                        "questionTypeCode": "judgment",
                                        "id": "2c9081729b834dff019b8931a9200849"
                                    },
                                    {
                                        "questionTitleAttachments": [],
                                        "attachments": [],
                                        "comments": "",
                                        "questionTitle": "6、强生公司决定为一个口味不同的牙线产品建立一个专门网站，其目的主要是使顾客有一个链接到强生公司官网主页上的机会。",
                                        "questionDiffCode": "2",
                                        "isAnswerCorrect": false,
                                        "studentItemScore": 0,
                                        "commentsAttachments": [],
                                        "optionList": [
                                            {
                                                "attachments": [],
                                                "content": "正确"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "错误"
                                            }
                                        ],
                                        "objectiveAnswer": "",
                                        "subjectiveAnswer": "",
                                        "itemScore": 3.4,
                                        "questionTypeName": "判断题",
                                        "questionDiffName": "中",
                                        "questionTypeCode": "judgment",
                                        "id": "2c9081729b834dff019b8931a920084a"
                                    },
                                    {
                                        "questionTitleAttachments": [],
                                        "attachments": [],
                                        "comments": "",
                                        "questionTitle": "7、企业在进行微媒体营销时，主要是向朋友群推送广告。",
                                        "questionDiffCode": "2",
                                        "isAnswerCorrect": false,
                                        "studentItemScore": 0,
                                        "commentsAttachments": [],
                                        "optionList": [
                                            {
                                                "attachments": [],
                                                "content": "正确"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "错误"
                                            }
                                        ],
                                        "objectiveAnswer": "",
                                        "subjectiveAnswer": "",
                                        "itemScore": 3.4,
                                        "questionTypeName": "判断题",
                                        "questionDiffName": "中",
                                        "questionTypeCode": "judgment",
                                        "id": "2c9081729b834dff019b8931a921084b"
                                    },
                                    {
                                        "questionTitleAttachments": [],
                                        "attachments": [],
                                        "comments": "",
                                        "questionTitle": "1、消费者的需求是",
                                        "questionDiffCode": "2",
                                        "isAnswerCorrect": false,
                                        "studentItemScore": 0,
                                        "commentsAttachments": [],
                                        "optionList": [
                                            {
                                                "attachments": [],
                                                "content": "没有得到某些基本满足的感受状态"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "想得到基本需要的具体满足物的愿望"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "对于有能力购买并且愿意购买的某个具体产品的欲望满足"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "对于愿意购买的某个产品的欲望"
                                            }
                                        ],
                                        "objectiveAnswer": "",
                                        "subjectiveAnswer": "",
                                        "itemScore": 3.4,
                                        "questionTypeName": "单选题",
                                        "questionDiffName": "中",
                                        "questionTypeCode": "single",
                                        "id": "2c9081729b834dff019b8931a921084c"
                                    },
                                    {
                                        "questionTitleAttachments": [],
                                        "attachments": [],
                                        "comments": "",
                                        "questionTitle": "2、“迪斯尼乐园的产品不是米老鼠、唐老鸭，而是快乐，”这体现的营销观念是",
                                        "questionDiffCode": "2",
                                        "isAnswerCorrect": false,
                                        "studentItemScore": 0,
                                        "commentsAttachments": [],
                                        "optionList": [
                                            {
                                                "attachments": [],
                                                "content": "产品观念"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "推销观念"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "市场观念"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "社会营销观念"
                                            }
                                        ],
                                        "objectiveAnswer": "",
                                        "subjectiveAnswer": "",
                                        "itemScore": 3.4,
                                        "questionTypeName": "单选题",
                                        "questionDiffName": "中",
                                        "questionTypeCode": "single",
                                        "id": "2c9081729b834dff019b8931a922084d"
                                    },
                                    {
                                        "questionTitleAttachments": [],
                                        "attachments": [],
                                        "comments": "",
                                        "questionTitle": "3、某拖拉机制造商决定自己生产轮胎，这属于",
                                        "questionDiffCode": "2",
                                        "isAnswerCorrect": false,
                                        "studentItemScore": 0,
                                        "commentsAttachments": [],
                                        "optionList": [
                                            {
                                                "attachments": [],
                                                "content": "后向一体化"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "前向一体化"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "水平一体化"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "水平多角化"
                                            }
                                        ],
                                        "objectiveAnswer": "",
                                        "subjectiveAnswer": "",
                                        "itemScore": 3.4,
                                        "questionTypeName": "单选题",
                                        "questionDiffName": "中",
                                        "questionTypeCode": "single",
                                        "id": "2c9081729b834dff019b8931a922084e"
                                    },
                                    {
                                        "questionTitleAttachments": [],
                                        "attachments": [],
                                        "comments": "",
                                        "questionTitle": "4、美国桂格麦片公司成功地推出桂格超脆麦片后，又利用这一品牌及其图样特征，推出雪糕、运动衫等新产品，其使用了",
                                        "questionDiffCode": "2",
                                        "isAnswerCorrect": false,
                                        "studentItemScore": 0,
                                        "commentsAttachments": [],
                                        "optionList": [
                                            {
                                                "attachments": [],
                                                "content": "家族品牌策略"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "品牌扩展决策"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "品牌战略决策"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "品牌再定位决策"
                                            }
                                        ],
                                        "objectiveAnswer": "",
                                        "subjectiveAnswer": "",
                                        "itemScore": 3.4,
                                        "questionTypeName": "单选题",
                                        "questionDiffName": "中",
                                        "questionTypeCode": "single",
                                        "id": "2c9081729b834dff019b8931a923084f"
                                    },
                                    {
                                        "questionTitleAttachments": [],
                                        "attachments": [],
                                        "comments": "",
                                        "questionTitle": "10、以下哪些是成本加成定价法的好处",
                                        "questionDiffCode": "2",
                                        "isAnswerCorrect": false,
                                        "studentItemScore": 0,
                                        "commentsAttachments": [],
                                        "optionList": [
                                            {
                                                "attachments": [],
                                                "content": "提高厂商的利润"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "依照成本来定价，使企业的成本都能得到补偿"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "如果所有企业都使用该方法，价格都很接近且价格竞争降到了很低程度"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "许多人觉得成本加成定价对各方公平"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "该方法重视市场需求"
                                            }
                                        ],
                                        "objectiveAnswer": "",
                                        "subjectiveAnswer": "",
                                        "itemScore": 3.4,
                                        "questionTypeName": "多选题",
                                        "questionDiffName": "中",
                                        "questionTypeCode": "multiple",
                                        "id": "2c9081729b834dff019b8931a9230850"
                                    },
                                    {
                                        "questionTitleAttachments": [],
                                        "attachments": [],
                                        "comments": "",
                                        "questionTitle": "当产品处于成长阶段，可以运用的营销策略是",
                                        "questionDiffCode": "2",
                                        "isAnswerCorrect": false,
                                        "studentItemScore": 0,
                                        "commentsAttachments": [],
                                        "optionList": [
                                            {
                                                "attachments": [],
                                                "content": "改进产品品质"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "加强企业与产品竞争地位"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "调整产品的价格"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "改进营销组合"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "拓展新市场"
                                            }
                                        ],
                                        "objectiveAnswer": "",
                                        "subjectiveAnswer": "",
                                        "itemScore": 3.4,
                                        "questionTypeName": "多选题",
                                        "questionDiffName": "中",
                                        "questionTypeCode": "multiple",
                                        "id": "2c9081729b834dff019b8931a9240851"
                                    },
                                    {
                                        "questionTitleAttachments": [],
                                        "attachments": [],
                                        "comments": "",
                                        "questionTitle": "品牌是一个集合概念，它包括",
                                        "questionDiffCode": "2",
                                        "isAnswerCorrect": false,
                                        "studentItemScore": 0,
                                        "commentsAttachments": [],
                                        "optionList": [
                                            {
                                                "attachments": [],
                                                "content": "品牌图案"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "品牌名称"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "品牌标志"
                                            },
                                            {
                                                "attachments": [],
                                                "content": ".品牌文化"
                                            },
                                            {
                                                "attachments": [],
                                                "content": "商标"
                                            }
                                        ],
                                        "objectiveAnswer": "",
                                        "subjectiveAnswer": "",
                                        "itemScore": 3.4,
                                        "questionTypeName": "多选题",
                                        "questionDiffName": "中",
                                        "questionTypeCode": "multiple",
                                        "id": "2c9081729b834dff019b8931a9240852"
                                    }
                                ],
                                "id": "2c9081729b834dff019b8931a9150835",
                                "title": "平时作业"
                            }
                        ]
                """;
    }

}

@Data
class Paper {
    private String id;
    private List<Question> childList;
}

@Data
class Question {
    private String id;
    @JsonAlias("questionTypeCode")
    @JsonProperty("topicType")
    private String topicType;
    private String answer;
}
