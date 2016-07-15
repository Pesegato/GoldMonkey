package com.pesegato.goldmonkey;

class GoldNumber {
    String id;
    Number value;
    GoldNumber(String id, int value){
        this.id=id;
        this.value=value;
    }
    GoldNumber(String id, float value){
        this.id=id;
        this.value=value;
    }

    Number getValue(){
        return value;
    }
}
