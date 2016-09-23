package com.example.pefami.benpaob.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/9/21.
 */
public class City {

    /**
     * pinyin : A
     */

    private List<CityListBean> cityList;

    public List<CityListBean> getCityList() {
        return cityList;
    }

    public void setCityList(List<CityListBean> cityList) {
        this.cityList = cityList;
    }

    public static class CityListBean {
        private String pinyin;
        /**
         * name : 阿拉尔市
         * en_name : alaer
         * car_prefix : 新N
         * openEconomy : false
         * pinyin : A
         * city_id : 356
         * pre_plate_no : 新
         */

        private List<ListsBean> lists;

        public String getPinyin() {
            return pinyin;
        }

        public void setPinyin(String pinyin) {
            this.pinyin = pinyin;
        }

        public List<ListsBean> getLists() {
            return lists;
        }

        public void setLists(List<ListsBean> lists) {
            this.lists = lists;
        }

        public static class ListsBean {
            private String name;
            private String en_name;
            private String car_prefix;
            private boolean openEconomy;
            private String pinyin;
            private int city_id;
            private String pre_plate_no;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getEn_name() {
                return en_name;
            }

            public void setEn_name(String en_name) {
                this.en_name = en_name;
            }

            public String getCar_prefix() {
                return car_prefix;
            }

            public void setCar_prefix(String car_prefix) {
                this.car_prefix = car_prefix;
            }

            public boolean isOpenEconomy() {
                return openEconomy;
            }

            public void setOpenEconomy(boolean openEconomy) {
                this.openEconomy = openEconomy;
            }

            public String getPinyin() {
                return pinyin;
            }

            public void setPinyin(String pinyin) {
                this.pinyin = pinyin;
            }

            public int getCity_id() {
                return city_id;
            }

            public void setCity_id(int city_id) {
                this.city_id = city_id;
            }

            public String getPre_plate_no() {
                return pre_plate_no;
            }

            public void setPre_plate_no(String pre_plate_no) {
                this.pre_plate_no = pre_plate_no;
            }
        }
    }
}
