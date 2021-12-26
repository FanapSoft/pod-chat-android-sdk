package com.example.chat.application.chatexample.enums;

public enum ServerType {
    Main {
        @Override
        public String toString() {
            return "Main";
        }
    }, Integration {
        @Override
        public String toString() {
            return "Integration";
        }
    }, Sandbox {
        @Override
        public String toString() {
            return "Sandbox";
        }
    }


}
