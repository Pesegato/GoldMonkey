package com.pesegato.goldmonkey;

class GoldString {
    String id;
    String string;
    GoldString(String id, String string){
        this.id=id;
        this.string=string;
    }

    String getString(){
        return string;
    }
}
