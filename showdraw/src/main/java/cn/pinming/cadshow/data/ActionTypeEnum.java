package cn.pinming.cadshow.data;

public enum ActionTypeEnum {
        YES(1, "可操作"),
        NO(2, "不可操作"),
        LOCAL(3, "不可操作，并且菜单栏有变化")
        ;
        // //声音类型 1有声音 2无声音
        private String strName;
        private Integer value;

        private ActionTypeEnum(Integer value, String strName) {
            this.value = value;
            this.strName = strName;
        }

        public String strName() {
            return strName;
        }

        public Integer value() {
            return value;
        }
    }