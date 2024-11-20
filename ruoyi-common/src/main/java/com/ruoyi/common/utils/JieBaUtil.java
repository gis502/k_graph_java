package com.ruoyi.common.utils;

import com.huaban.analysis.jieba.JiebaSegmenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Springboot项目整合jieba分词，实现语句最精确的切分
 */
public class JieBaUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JieBaUtil.class);

    private static JiebaSegmenter segmenter = new JiebaSegmenter();

    /**
     * 获取分词内容
     *
     * @param words
     * @return
     */
    public static List<String> getSignaleWord(String words) {

        // 匹配分词模板 使用自定义分词字典时使用
        // String[] sentences = new String[]{"/energe.dict.utf8"};
        // segmenter.initUserDict(sentences);
        List<String> resultList = segmenter.sentenceProcess(words);
        return resultList;
    }

    /*
     * 对分词结果进行统计
     * */
    public static Map<String, Integer> countWords(List<String> words) {
        Map<String, Integer> wordCount = new HashMap<>();
        // 正则表达式，用于去除特殊符号和空格
        String regex = "[\\u4e00-\\u9fa5a-zA-Z0-9]+";
        Pattern pattern = Pattern.compile(regex);



        for (String word : words) {
            // 将单词转为小写，以实现不区分大小写的统计
            String lowerCaseWord = word.toLowerCase();
            // 去除单词中的特殊符号和空格
            String cleanedWord = pattern.matcher(lowerCaseWord).replaceAll("");
            // 分割单词，以统计每个独立的单词
            String[] tokens = cleanedWord.split("\\s+");
            for (String token : tokens) {
                if (!token.isEmpty()) {
                    wordCount.put(token, wordCount.getOrDefault(token, 0) + 1);
                }
            }
        }
        return wordCount;
    }


    public static void main(String[] args) {

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            list.add("词云");
            list.add("Vue");
            list.add("ECharts");
            list.add("JavaScript");
            list.add("前端");
            list.add("开发");
            list.add("可视化被子");
            list.add("褥子");
            list.add("手电筒");
            list.add("口罩");
            list.add("口罩");
            list.add("袜子");
            list.add("球衣");
            list.add("医生");
            list.add("护士");
            list.add("人手");
            list.add("B型血");
            list.add("AB型血");
            list.add("O型血");
            list.add("摄像机");
            list.add("感冒药");
            list.add("999感冒灵");
            list.add("复方氨醯胶囊");
            list.add("发烧药");
            list.add("止血药");
            list.add("止痛药");
            list.add("止痛片");
            list.add("大量药品");
        }

        String res = list.stream().collect(Collectors.joining(""));

        LOGGER.debug("分词集合：" + JieBaUtil.getSignaleWord(res));

        List<String> signaleWord = JieBaUtil.getSignaleWord(res);

        Map<String, Integer> wordCount = countWords(signaleWord);

        LOGGER.debug("分词结果：", wordCount);

        wordCount.forEach((word, count) -> System.out.println(word + ": " + count));

    }


}
