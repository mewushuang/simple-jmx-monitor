package com.van.entry;

public class Packet {
        private String module;
        private String key;
        private String value;

        public Packet(String module, String key, String value) {
            this.module = module;
            this.key = key;
            this.value = value;
        }

        public String module() {
            return module;
        }

        public String key() {
            return key;
        }

        public String value() {
            return value;
        }

    }