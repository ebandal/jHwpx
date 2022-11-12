package HwpDoc.paragraph;

public class ParaText extends Ctrl {
    public String  text;
    public int     startIdx;

    public ParaText(String ctrlId, String text, int startIdx) {
        super(ctrlId);
        this.text = text;
        this.startIdx = startIdx;
    }
    
    @Override
    public int getSize() {
        return text==null?0:text.length();
    }

}
