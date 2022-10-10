/* MIT License
 *  
 * Copyright (c) 2022 ebandal
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 * 본 제품은 한글과컴퓨터의 ᄒᆞᆫ글 문서 파일(.hwp) 공개 문서를 참고하여 개발하였습니다.
 * 개방형 워드프로세서 마크업 언어(OWPML) 문서 구조 KS X 6101:2018 문서를 참고하였습니다.
 * 작성자 : 반희수 ebandal@gmail.com  
 * 작성일 : 2022.10
 */
package HwpDoc.paragraph;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import HwpDoc.Exception.NotImplementedException;
import HwpDoc.HwpElement.HwpRecordTypes.LineType2;
import HwpDoc.HwpElement.HwpRecord_BorderFill;
import HwpDoc.HwpElement.HwpRecord_BorderFill.Fill;
import HwpDoc.paragraph.Ctrl_Common.VertAlign;
import HwpDoc.paragraph.Ctrl_Table.CellZone;

public class Ctrl_GeneralShape extends Ctrl_ObjElement {
	private static final Logger log = Logger.getLogger(Ctrl_GeneralShape.class.getName());
	private HwpParagraph	parent;
	private int	size;

	// 테두리선 정보
	public int		lineColor;	// 선색상
	public short	lineThick;	// 선굵기
	public int		lineAttr;	// 테두리선 정보 속성
	public LineType2	lineType;	
	public byte		outline;	// Outline style
	
	// 채우기 정보
	public int		fillType;	// 채우기 종류 (0:없음, 1:단색, 2:이미지, 4:그라데이션)
	public Fill		fill;		// 채우기
	
	// 글상자 텍스트 속성
	public short	leftSpace;	// 글상자 텍스트 왼쪽 여백
	public short	rightSpace;	// 글상자 텍스트 오른쪽 여백
	public short	upSpace;	// 글상자 텍스트 위쪽 여백
	public short 	downSpace;	// 글상자 텍스트 아래쪽 여백
	public int		maxTxtWidth;// 텍스트 문자열 최대 폭

	public Ctrl_GeneralShape() {
		super();
	}

	public Ctrl_GeneralShape(String ctrlId, int size, byte[] buf, int off, int version) {
		super(ctrlId, size, buf, off, version);
		this.size = offset-off;

		log.fine("                                                  " + toString());
	}
	
	public Ctrl_GeneralShape(Ctrl_GeneralShape shape) {
		super((Ctrl_ObjElement)shape);
		this.parent			= shape.parent;
		
		this.lineColor 		= shape.lineColor;
		this.lineThick 		= shape.lineThick;
		this.lineAttr 		= shape.lineAttr;
		this.lineType		= shape.lineType;
		this.outline 		= shape.outline;
		this.fillType 		= shape.fillType;
		this.fill 			= shape.fill;
		this.leftSpace 		= shape.leftSpace;
		this.rightSpace 	= shape.rightSpace;
		this.upSpace 		= shape.upSpace;
		this.downSpace 		= shape.downSpace;
		this.maxTxtWidth 	= shape.maxTxtWidth;
	}
	
	public Ctrl_GeneralShape(String ctrlId, Node node, int version) throws NotImplementedException {
	    super(ctrlId, node, version);
	    
	    String numStr;
	    
        NodeList nodeList = node.getChildNodes();
        for (int i=0; i<nodeList.getLength(); i++) {
            Node child = nodeList.item(i);
            switch(child.getNodeName()) {
            case "hp:lineShape":    // 그리기 객체의 테두리선 정보
                {
                    NamedNodeMap childAttrs = child.getAttributes();
                    numStr = childAttrs.getNamedItem("color").getNodeValue().replaceAll("#", "");   // 선색상
                    lineColor = Integer.parseInt(numStr);
                    numStr = childAttrs.getNamedItem("width").getNodeValue();                       // 선 굵기
                    lineThick = (short) Integer.parseInt(numStr);
                    lineType = LineType2.valueOf(childAttrs.getNamedItem("style").getNodeValue()); // 선 종류
                    /*
                    childAttrs.getNamedItem("endCap").getNodeValue();                      // 선 끝 모양
                    childAttrs.getNamedItem("headStyle").getNodeValue();                   // 화살표 시작 모양
                    childAttrs.getNamedItem("tailStyle").getNodeValue();                   // 화살표 끝 모양
                    childAttrs.getNamedItem("headSz").getNodeValue();                      // 화살표 시작 크기
                    childAttrs.getNamedItem("tailSz").getNodeValue();                      // 화살표 끝 크기
                    childAttrs.getNamedItem("outlineStyle").getNodeValue();                // 테두리선의 형태
                    childAttrs.getNamedItem("alpha").getNodeValue();                       // 투명도
                    */       
                }
                break;
            case "hp:fillBrush":    // 그리기 객체의 채우기 정보
                fill = HwpRecord_BorderFill.readFillBrush(child);
                break;
            case "hp:drawText":     // 그리기 객체 글상자용 텍스트   178 page
                {
                    NamedNodeMap childAttrs = child.getAttributes();
                    numStr = childAttrs.getNamedItem("lastWidth").getNodeValue();   // 텍스트 문자열의 최대 폭
                    maxTxtWidth = Integer.parseInt(numStr);
                    /*
                    childAttrs.getNamedItem("width").getNodeValue();  // 글 상자 이름
                    childAttrs.getNamedItem("style").getNodeValue()); // 편집 가능 여부
                    */
                    NodeList childNodeList = child.getChildNodes();
                    for (int j=0; j<childNodeList.getLength(); j++) {
                        Node grandChild = childNodeList.item(j);
                        switch(grandChild.getNodeName()) {  // 179 page
                        case "hp:textMargin":
                            NamedNodeMap grandAttrs = child.getAttributes();
                            numStr = grandAttrs.getNamedItem("left").getNodeValue();
                            leftSpace = (short) Integer.parseInt(numStr);
                            numStr = grandAttrs.getNamedItem("right").getNodeValue();
                            rightSpace = (short) Integer.parseInt(numStr);
                            numStr = grandAttrs.getNamedItem("top").getNodeValue();
                            upSpace = (short) Integer.parseInt(numStr);
                            numStr = grandAttrs.getNamedItem("bottom").getNodeValue();
                            downSpace = (short) Integer.parseInt(numStr);
                            break;
                        case "hp:subList":
                            do_subList(grandChild, version);
                            break;
                        }
                    }
                }
                break;
            case "hp:shadow":       // 그리기 객체의 그림자 정보
                /*
                NamedNodeMap childAttrs = child.getAttributes();
                childAttrs.getNamedItem("type").getNodeValue();
                childAttrs.getNamedItem("color").getNodeValue();
                childAttrs.getNamedItem("offsetX").getNodeValue();
                childAttrs.getNamedItem("offsetY").getNodeValue();
                childAttrs.getNamedItem("alpha").getNodeValue();
                */
                break;
            }
        }
    }


	private void do_subList(Node node, int version) throws NotImplementedException {
        if (paras==null) {
            paras = new ArrayList<HwpParagraph>();
        }
        NodeList nodeList = node.getChildNodes();
        for (int i=0; i<nodeList.getLength(); i++) {
            Node child = nodeList.item(i);
            switch(child.getNodeName()) {
            case "hp:p":
                HwpParagraph p = new HwpParagraph(child, version);
                paras.add(p);
                break;
            }
        }
	}
	
    public String toString() {
		StringBuffer strb = new StringBuffer();
		strb.append("CTRL("+ctrlId+")")
			.append("=공통속성:"+super.toString());
		return strb.toString();
	}

	public void setParent(HwpParagraph para) {
		this.parent = para;
	}
	public HwpParagraph getParent() {
		return parent;
	}
	
	@Override
	public int getSize() {
		return size;
	}
}
