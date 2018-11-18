package lstatcore;

public enum Language {
    EN, DE;

    public String toString(){
        switch(this){
            case EN:
                return "en-US";
            case DE:
                return "de-DE";
            default:
                throw  new IllegalArgumentException();
        }
    }
}
