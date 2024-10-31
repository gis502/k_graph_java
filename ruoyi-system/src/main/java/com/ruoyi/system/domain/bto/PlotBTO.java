package com.ruoyi.system.domain.bto;

import lombok.Data;

import java.util.List;

@Data
public class PlotBTO {
    private List<Sheet> sheets;

    @Data
    public static class Sheet { // 使用 static 修饰内部类
        private String name;
        private List<Field> fields;
    }

    @Data
    public static class Field { // 使用 static 修饰内部类
        private String name;
        private String type;
        private List<Content> content; // 只有在 type 为 "select" 时才会有内容
    }

    @Data
    public static class Content { // 使用 static 修饰内部类
        private String value;
        private String label;
    }
}
