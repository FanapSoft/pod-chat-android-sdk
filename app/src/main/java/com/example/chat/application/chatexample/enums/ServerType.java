package com.example.chat.application.chatexample.enums;

public enum ServerType {
    Main {
        @Override
        public String toString() {
            return "Main";
        }
    },
    Integration {
        @Override
        public String toString() {
            return "Integration";
        }
    }, KafkaTest {
        @Override
        public String toString() {
            return "KafkaTest";
        }
    }, Sandbox {
        @Override
        public String toString() {
            return "Sandbox";
        }
    }


}
