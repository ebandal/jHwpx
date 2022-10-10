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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import HwpDoc.Exception.HwpParseException;
import HwpDoc.Exception.NotImplementedException;
import HwpDoc.HwpElement.HwpRecordTypes.LineType2;
import HwpDoc.HwpElement.HwpRecord_BorderFill;
import HwpDoc.HwpElement.HwpRecord_BorderFill.Fill;

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
                            NamedNodeMap grandAttrs = grandChild.getAttributes();
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
	
	public static Ctrl_GeneralShape parse(Ctrl_GeneralShape obj, int size, byte[] buf, int off, int version) throws HwpParseException, NotImplementedException {
        int offset = off;
        
        // hwp포맷에는  역순으로 ctrlId를 구성한다. 여기서는 순방향으로 구성한다.
        String ctrlId = new String(buf, offset, 4, StandardCharsets.US_ASCII);
        offset += 4;
        Ctrl_GeneralShape shape = null;
        
        log.fine("                                                  ctrlID="+ctrlId);
        // ctrlId를 거꾸로 읽어 비교한다.
        switch(ctrlId) {
        case "cip$":    // 그림       ShapePic obj = new ShapePic(shape);
            shape = new Ctrl_ShapePic(obj);
            offset += Ctrl_ShapePic.parseCtrl((Ctrl_ShapePic)shape, size-(offset-off), buf, offset, version);
            shape.ctrlId = ctrlId;
            break;
        case "cer$":    // 사각형
            shape = new Ctrl_ShapeRect(obj);
            offset += Ctrl_ShapeRect.parseCtrl((Ctrl_ShapeRect)shape, size-(offset-off), buf, offset, version);
            shape.ctrlId = ctrlId;
            break;
        case "nil$":    // 선
        case "loc$":    // 개체연결선
            shape = new Ctrl_ShapeLine(obj);
            offset += Ctrl_ShapeLine.parseCtrl((Ctrl_ShapeLine)shape, size-(offset-off), buf, offset, version);
            shape.ctrlId = ctrlId;
            break;
        case "noc$":    // 묶음 개체
            shape = new Ctrl_Container(obj);
            offset += Ctrl_Container.parseCtrl((Ctrl_Container)shape, size-(offset-off), buf, offset, version);
            shape.ctrlId = ctrlId;
            break;
        case "lle$":    // 타원
            shape = new Ctrl_ShapeEllipse(obj);
            offset += Ctrl_ShapeEllipse.parseCtrl((Ctrl_ShapeEllipse)shape, size-(offset-off), buf, offset, version);
            shape.ctrlId = ctrlId;
            break;
        case "lop$":    // 다각형
            shape = new Ctrl_ShapePolygon(obj);
            offset += Ctrl_ShapePolygon.parseCtrl((Ctrl_ShapePolygon)shape, size-(offset-off), buf, offset, version);
            shape.ctrlId = ctrlId;
            break;
        case "cra$":    // 호
            shape = new Ctrl_ShapeArc(obj);
            offset += Ctrl_ShapeArc.parseCtrl((Ctrl_ShapeArc)shape, size-(offset-off), buf, offset, version);
            shape.ctrlId = ctrlId;
            break;
        case "ruc$":    // 곡선
            shape = new Ctrl_ShapeCurve(obj);
            offset += Ctrl_ShapeCurve.parseCtrl((Ctrl_ShapeCurve)shape, size-(offset-off), buf, offset, version);
            shape.ctrlId = ctrlId;
            break;
        case "deqe":    // 한글97 수식
            shape = new Ctrl_EqEdit(obj);
            offset += Ctrl_EqEdit.parseCtrl((Ctrl_EqEdit)shape, size-(offset-off), buf, offset, version);
            shape.ctrlId = ctrlId;
        case "elo$":    // OLE
            shape = new Ctrl_ShapeOle(obj);
            offset += Ctrl_ShapeOle.parseCtrl((Ctrl_ShapeOle)shape, size-(offset-off), buf, offset, version);
            shape.ctrlId = ctrlId;
            break;
        case "div$":    // Video
            shape = new Ctrl_ShapeVideo(obj);
            offset += Ctrl_ShapeVideo.parseCtrl((Ctrl_ShapeVideo)shape, size-(offset-off), buf, offset, version);
            shape.ctrlId = ctrlId;
            break;
        case "tat$":    // TextArt(글맵시)
            shape = new Ctrl_ShapeTextArt(obj);
            offset += Ctrl_ShapeTextArt.parseCtrl((Ctrl_ShapeTextArt)shape, size-(offset-off), buf, offset, version);
            shape.ctrlId = ctrlId;
            break;
        default:
            log.severe("Neither known ctrlID=" + ctrlId+" nor implemented.");
            throw new NotImplementedException(ctrlId);
        }
        
        if (offset-off-size!=0) {
            log.fine("[CtrlID]=" + ctrlId + ", size=" + size + ", but currentSize=" + (offset-off));
            // throw new HwpParseException();
        }
        
        return shape;
    }

    public static int parseListHeaderAppend(Ctrl_GeneralShape obj, int size, byte[] buf, int off, int version) throws HwpParseException, NotImplementedException {
        int offset = off;
        if (size>=16) {
            offset += 2;
            obj.captionAttr     = buf[offset+3]<<24&0xFF000000 | buf[offset+2]<<16&0x00FF0000 | buf[offset+1]<<8&0x0000FF00 | buf[offset]&0x000000FF;
            offset += 4;
            obj.captionWidth    = buf[offset+3]<<24&0xFF000000 | buf[offset+2]<<16&0x00FF0000 | buf[offset+1]<<8&0x0000FF00 | buf[offset]&0x000000FF;
            offset += 4;
            obj.captionSpacing  = (short) (buf[offset+1]<<8&0xFF00 | buf[offset]&0x00FF);
            offset += 2;
            obj.captionMaxW     = buf[offset+3]<<24&0xFF000000 | buf[offset+2]<<16&0x00FF0000 | buf[offset+1]<<8&0x0000FF00 | buf[offset]&0x000000FF;
            offset += 4;
        }
        if (size-(offset-off)==8) {
            offset += 8;
        }
        
        log.fine("                                                  ctrlID="+obj.ctrlId+", 캡션 parsing이지만, 정확한 parsing은 어떻게 해야 하는지 알 수 없음.");
        
        if (offset-off-size!=0) {
            log.fine("[CtrlID]=" + obj.ctrlId + ", size=" + size + ", but currentSize=" + (offset-off));
            throw new HwpParseException();
        }
        
        return offset-off;
    }

    public static int parseCtrl(Ctrl_GeneralShape obj, int size, byte[] buf, int off, int version) throws HwpParseException {
        int offset = off;
        
        int len = Ctrl_ObjElement.parseCtrl((Ctrl_ObjElement)obj, size, buf, offset, version);
        offset += len;

        obj.lineColor   = buf[offset+3]<<24&0xFF000000 | buf[offset+2]<<16&0x00FF0000 | buf[offset+1]<<8&0x0000FF00 | buf[offset]&0x000000FF;
        offset += 4;
        obj.lineThick   = (short) (buf[offset+1]<<8&0xFF00 | buf[offset]&0x00FF);
        offset += 2;

        // 문서와 다르게  선 굵기에서 4byte 후에 선 속성이 온다.
        offset += 2;
        
        obj.lineAttr    = buf[offset+3]<<24&0xFF000000 | buf[offset+2]<<16&0x00FF0000 | buf[offset+1]<<8&0x0000FF00 | buf[offset]&0x000000FF;
        offset += 4;
        obj.lineType    = LineType2.from(obj.lineAttr&0x3F);
        obj.outline     = buf[offset++];
        
        obj.fill = new Fill(buf, offset, size-(offset-off)-22);
        offset += obj.fill.getSize();
        
        // 글상자 텍스트 속성.  아래 내용대로 읽히지 않는다.  알 수 없는 22bytes가 온다.
        offset += 22;
        
        log.finest("[그리기 개체 공통 속성]을 읽었습니다.");
        log.finest("[그리기 개체 글상자용 텍스트 속성]을 읽었습니다. [문단 리스트 헤더]를 읽어야 합니다.");
        
        return offset-off;
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
