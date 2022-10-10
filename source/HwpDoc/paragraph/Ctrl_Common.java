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
import java.util.List;
import java.util.logging.Logger;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import HwpDoc.Exception.NotImplementedException;

public class Ctrl_Common extends Ctrl {
	private static final Logger log = Logger.getLogger(Ctrl_Common.class.getName());
	private int size;
	protected int offset;
	
	public int         objAttr;		// 개체 공통 속성 (표 70참조)
	public boolean     treatAsChar;	// 글자처럼 취급 여부
	public boolean     affectLSpacing;	// 줄 간격에 영향을 줄지 여부 (TreatAsChar가 true일때만 사용)
	public VRelTo      vertRelTo;		// 세로위치기준 (TreatAsChar가 false일때만 사용)
	public VertAlign   vertAlign;		// VertRelTo에 대한 상대적인 배열방식. VertRelTo의 값에 따라 가능한 범위가 제한된다. (VertRelTo가 "Para"인 경우 "Para"값만 가능, 나머지 경우에는 모든 값 가능)
	public HRelTo      horzRelTo;		// 가로 위치의 기준 (TreatAsChar가 false일때만 사용)
	public HorzAlign   horzAlign;		// HorzRelTo에 대한 상대적이 배열방식
	public boolean     flowWithText;	// 오브젝트의 세로 위치를 본문 영역으로 제한할지 여부 (VertRelTo가 Para일때만 사용)
	public boolean     allowOverlap;	// 다른 오브젝트와 겹치는 것을 허용할지 여부. (TreatAsChar가 false일대만 사용, flowWithText가 true이면 언제나 false로 간주함)
	public WidthRelTo  widthRelto;		// 오브젝트 폭의 기준
	public HeightRelTo heightRelto;	// 오브젝트 높이의 기준
	public byte        wrapStyle;		// 0:Squre, 1:Tight, 2:Through, 3:TopAndBottom, 4:BehindText, 5:InFrontOfText
	public byte        wrapText;		// 0:양쪽, 1:왼쪽, 2:오른쪽, 3:큰쪽
	public byte        numberingType;  // 이 객체가 속하는 번호 범위
	
	public int         vertOffset;		// 세로 오프셋 값
	public int         horzOffset;		// 가로 오프셋 값
	public int         width;			// width 오브젝트의 폭
	public int         height;			// height 오브젝트의 높이
	public int         zOrder;		
	public short[]     outMargin;
	public int         objInstanceID;	// 문서 내 각 개체에 대한 고유 아이디(instance ID)
	public int         blockPageBreak;	// 쪽나눔 방지 on(1)/off(0)
	public String      objDesc;		    // 개체 설명문
	
	public List<HwpParagraph> paras;	// LIST_HEADER 뒤에 따라오는 PARA_HEADER (복수개)
	public int         captionAttr;		// 캡션 속성
	public int         captionWidth;	// 캡션 폭
	public int         captionSpacing;	// 캡션과 틀 사이 간격
	public int         captionMaxW;		// 텍스트의 최대 길이 (=개체의 폭)
	public List<CapParagraph> caption;	// 캡션이 담길 Paragraph
	
	public VertAlign   textVerAlign;	// [20210428] (shape)컨트롤 내  문단의 vertical align

	public Ctrl_Common() {
		super();
	}
	
	public Ctrl_Common(String ctrlId, int size, byte[] buf, int off, int version) {
		super(ctrlId);
		offset = off;
		this.ctrlId = ctrlId;
		
		objAttr	 		= buf[offset+3]<<24&0xFF000000 | buf[offset+2]<<16&0x00FF0000 | buf[offset+1]<<8&0x0000FF00 | buf[offset]&0x000000FF;
		offset += 4;
		treatAsChar		= (objAttr&0x01)==0x01?true:false;
		affectLSpacing	= (objAttr&0x04)==0x04?true:false;
		vertRelTo		= VRelTo.from(objAttr>>3&0x03);
		vertAlign		= VertAlign.from(objAttr>>5&0x07);
		horzRelTo		= HRelTo.from(objAttr>>8&0x03);
		horzAlign		= HorzAlign.from(objAttr>>10&0x07);
		flowWithText	= (objAttr&0x2000)==0x2000?true:false;
		allowOverlap	= (objAttr&0x4000)==0x4000?true:false;
		widthRelto		= WidthRelTo.from(objAttr>>15&0x07);
		heightRelto		= HeightRelTo.from(objAttr>>18&0x03);
		wrapStyle		= (byte) (objAttr>>21&0x07);
		wrapText		= (byte) (objAttr>>24&0x03);
		numberingType   = (byte) (objAttr>>26&0x07);
		
		vertOffset 		= buf[offset+3]<<24&0xFF000000 | buf[offset+2]<<16&0x00FF0000 | buf[offset+1]<<8&0x0000FF00 | buf[offset]&0x000000FF;
		offset += 4;
		horzOffset 		= buf[offset+3]<<24&0xFF000000 | buf[offset+2]<<16&0x00FF0000 | buf[offset+1]<<8&0x0000FF00 | buf[offset]&0x000000FF;
		offset += 4;
		width	 		= buf[offset+3]<<24&0xFF000000 | buf[offset+2]<<16&0x00FF0000 | buf[offset+1]<<8&0x0000FF00 | buf[offset]&0x000000FF;
		offset += 4;
		height		 	= buf[offset+3]<<24&0xFF000000 | buf[offset+2]<<16&0x00FF0000 | buf[offset+1]<<8&0x0000FF00 | buf[offset]&0x000000FF;
		offset += 4;
		zOrder	 		= buf[offset+3]<<24&0xFF000000 | buf[offset+2]<<16&0x00FF0000 | buf[offset+1]<<8&0x0000FF00 | buf[offset]&0x000000FF;
		offset += 4;
		outMargin = new short[4];
		for (int i=0;i<4;i++) {
		    outMargin[i]	= (short) (buf[offset+1]<<8&0xFF00 | buf[offset]&0x00FF);
			offset += 2;
		}
		objInstanceID	= buf[offset+3]<<24&0xFF000000 | buf[offset+2]<<16&0x00FF0000 | buf[offset+1]<<8&0x0000FF00 | buf[offset]&0x000000FF;
		offset += 4;
		blockPageBreak	= buf[offset+3]<<24&0xFF000000 | buf[offset+2]<<16&0x00FF0000 | buf[offset+1]<<8&0x0000FF00 | buf[offset]&0x000000FF;
		offset += 4;
		if (offset-off < size) {
			int descLen 	= (buf[offset+1]<<8&0xFF00 | buf[offset]&0x00FF)*2;
			offset += 2;
			if (descLen > 0) {
				objDesc = new String(buf, offset, descLen, StandardCharsets.UTF_16LE);
				offset += descLen;
			}
		}
		
		log.fine("                                                  " + toString());

		this.size = offset-off;
	}
	
	public Ctrl_Common(Ctrl_Common common) {
		super(common.ctrlId);
		this.objAttr 		= common.objAttr;
		this.treatAsChar 	= common.treatAsChar;
		this.affectLSpacing = common.affectLSpacing;
		this.vertRelTo 		= common.vertRelTo;
		this.vertAlign 		= common.vertAlign;
		this.horzRelTo 		= common.horzRelTo;
		this.horzAlign 		= common.horzAlign;
		this.flowWithText 	= common.flowWithText;
		this.allowOverlap 	= common.allowOverlap;
		this.widthRelto 	= common.widthRelto;
		this.heightRelto 	= common.heightRelto;
		this.wrapStyle		= common.wrapStyle;
		this.wrapText		= common.wrapText;
		this.numberingType  = common.numberingType;
		this.vertOffset 	= common.vertOffset;
		this.horzOffset 	= common.horzOffset;
		this.width 			= common.width;
		this.height 		= common.height;
		this.zOrder 		= common.zOrder;		
		this.outMargin 		= common.outMargin;
		this.objInstanceID 	= common.objInstanceID;
		this.blockPageBreak = common.blockPageBreak;
		this.objDesc 		= common.objDesc;
		this.paras 			= common.paras;
		this.captionAttr 	= common.captionAttr;
		this.captionWidth 	= common.captionWidth;
		this.captionSpacing = common.captionSpacing;
		this.captionMaxW 	= common.captionMaxW;
		this.caption 		= common.caption;
	}
	
	public Ctrl_Common(String ctrlId, Node node, int version) throws NotImplementedException {
	    super(ctrlId);
	    
        NamedNodeMap attributes = node.getAttributes();
        
        String numStr = attributes.getNamedItem("id").getNodeValue();
        objInstanceID = Integer.parseInt(numStr); 
        
        switch(attributes.getNamedItem("pageBreak").getNodeValue()) {
        case "TABLE":
        case "CELL":
        case "NONE":
            break;
        default:
            throw new NotImplementedException("Ctrl_Common");
        }

        switch(attributes.getNamedItem("textFlow").getNodeValue()) {
        case "BOTH_SIDES":  // 0:양쪽, 1:왼쪽, 2:오른쪽, 3:큰쪽
            wrapText = 0;   break; 
        case "LEFT_ONLY":
            wrapText = 1;   break; 
        case "RIGHT_ONLY":
            wrapText = 2;   break; 
        case "LARGEST_ONLY":
            wrapText = 3;   break; 
        }

        switch(attributes.getNamedItem("textWrap").getNodeValue()) {
        case "SQUARE":              // bound rect를 따라
            wrapStyle = 0;  break;
        case "TIGHT":               // 오브젝트의 outline을 따라
            wrapStyle = 1;  break;
        case "THROUGH":             // 오브젝트 내부의 빈 공간까지
            wrapStyle = 2;  break;
        case "TOP_AND_BOTTOM":      // 좌, 우에는 텍스트를 배치하지 않음.
            wrapStyle = 3;  break;
        case "BEHIND_TEXT":         // 글과 겹치게 하여 글 뒤로
            wrapStyle = 4;  break;
        case "IN_FRONT_OF_TEXT":    // 글과 겹치게 하여 글 앞으로
            wrapStyle = 5;  break;
        }

        numStr = attributes.getNamedItem("zOrder").getNodeValue();
        zOrder = Integer.parseInt(numStr);

        switch(attributes.getNamedItem("numberingType").getNodeValue()) {
        case "NONE":
            numberingType = 0;  break;
        case "FIGURE":
            numberingType = 1;  break;
        case "TABLE":
            numberingType = 2;  break;
        case "EQUATION":
            numberingType = 3;  break;
        }

        NodeList nodeList = node.getChildNodes();
        for (int i=0; i<nodeList.getLength(); i++) {
            Node child = nodeList.item(i);
            switch(child.getNodeName()) {
            case "hp:sz":
                {
                    NamedNodeMap childAttrs = child.getAttributes();
                    numStr = childAttrs.getNamedItem("width").getNodeValue();
                    width = Integer.parseInt(numStr);
                    widthRelto = WidthRelTo.valueOf(childAttrs.getNamedItem("widthRelTo").getNodeValue());
                    numStr = childAttrs.getNamedItem("height").getNodeValue();
                    height = (short) Integer.parseInt(numStr);
                    heightRelto = HeightRelTo.valueOf(childAttrs.getNamedItem("heightRelTo").getNodeValue());
                    // childAttrs.getNamedItem("protect").getNodeValue();
                }
                break;
            case "hp:pos":
                {
                    NamedNodeMap childAttrs = child.getAttributes();
                    switch(childAttrs.getNamedItem("treatAsChar").getNodeValue()) {
                    case "0":
                        treatAsChar = false;    break;
                    case "1":
                        treatAsChar = true;     break;
                    }
                    
                    if (treatAsChar) {
                        switch(childAttrs.getNamedItem("affectLSpacing").getNodeValue()) {
                        case "0":
                            affectLSpacing = false; break;
                        case "1":
                            affectLSpacing = true;  break;
                        }
                    } else {
                        switch(childAttrs.getNamedItem("allowOverlap").getNodeValue()) {
                        case "0":
                            allowOverlap = false; break;
                        case "1":
                            allowOverlap = true;  break;
                        }
                        
                        vertRelTo = VRelTo.valueOf(childAttrs.getNamedItem("vertRelTo").getNodeValue());
                        horzRelTo = HRelTo.valueOf(childAttrs.getNamedItem("horzRelTo").getNodeValue());
                    }
                    
                    if (vertRelTo==VRelTo.PARA) {
                        switch(childAttrs.getNamedItem("flowWithText").getNodeValue()) {
                        case "0":
                            flowWithText = false; break;
                        case "1":
                            flowWithText = true;  break;
                        }
                    }

                    // childAttrs.getNamedItem("holdAnchorAndSO").getNodeValue();

                    vertAlign = VertAlign.valueOf(childAttrs.getNamedItem("vertAlign").getNodeValue());
                    horzAlign = HorzAlign.valueOf(childAttrs.getNamedItem("horzAlign").getNodeValue());
                    
                    numStr = childAttrs.getNamedItem("vertOffset").getNodeValue();
                    vertOffset = Integer.parseInt(numStr);

                    numStr = childAttrs.getNamedItem("horzOffset").getNodeValue();
                    horzOffset = Integer.parseInt(numStr);
                }
                break;
            case "hp:outMargin":
                {
                    if (outMargin==null) {
                        outMargin = new short[4];
                    }
                    NamedNodeMap childAttrs = child.getAttributes();
                    numStr = childAttrs.getNamedItem("left").getNodeValue();
                    outMargin[0] = (short) Integer.parseInt(numStr);
                    numStr = childAttrs.getNamedItem("right").getNodeValue();
                    outMargin[1] = (short) Integer.parseInt(numStr);
                    numStr = childAttrs.getNamedItem("top").getNodeValue();
                    outMargin[2] = (short) Integer.parseInt(numStr);
                    numStr = childAttrs.getNamedItem("bottom").getNodeValue();
                    outMargin[3] = (short) Integer.parseInt(numStr);
                }
                break;
            case "hp:caption":
                setCaption(child, version);
                break;
            case "hp:shapeComment":
                break;
            }
        }
    }
	
	private void setCaption(Node node, int version) throws NotImplementedException {
        NamedNodeMap attrs = node.getAttributes();
        switch(attrs.getNamedItem("side").getNodeValue()) {
        case "LEFT":
            captionAttr = 0b00;      break;
        case "RIGHT":
            captionAttr = 0b01;      break;
        case "TOP":
            captionAttr = 0b10;      break;
        case "BOTTOM":
            captionAttr = 0b11;      break;
        }

        switch(attrs.getNamedItem("fullSize").getNodeValue()) {
        case "0":
            break;
        case "1":
            captionAttr |= 0b100;    break;
        }

        String numStr = attrs.getNamedItem("width").getNodeValue();
        captionWidth = Integer.parseInt(numStr);

        numStr = attrs.getNamedItem("gap").getNodeValue();
        captionSpacing = Integer.parseInt(numStr);
        
        numStr = attrs.getNamedItem("lastWidth").getNodeValue();
        captionMaxW = Integer.parseInt(numStr);
        
        if (caption==null) {
            caption = new ArrayList<>();
        }
        NodeList nodeList = node.getChildNodes();
        for (int i=0; i<nodeList.getLength(); i++) {
            Node child = nodeList.item(i);
            
            switch(child.getNodeName()) {
            case "subList":
                {
                    NamedNodeMap childAttrs = child.getAttributes();
                    // childAttrs.getNamedItem("id").getNodeValue();
                    // childAttrs.getNamedItem("textDirection").getNodeValue();
                    // childAttrs.getNamedItem("lineWrap").getNodeValue();
                    // childAttrs.getNamedItem("vertAlign").getNodeValue();
                    // childAttrs.getNamedItem("linkListIDRef").getNodeValue();
                    // childAttrs.getNamedItem("linkListNextIDRef").getNodeValue();
                    
                    NodeList childNodeList = child.getChildNodes();
                    for (int j=0; j<childNodeList.getLength(); j++) {
                        Node grandChild = childNodeList.item(j);
                        switch(grandChild.getNodeName()) {
                        case "p":
                            CellParagraph cellP = new CellParagraph(grandChild, version);
                            paras.add(cellP);
                            break;
                        }
                    }
                }
                break;
            }
        }
	}


    public String toString() {
		StringBuffer strb = new StringBuffer();
		strb.append("={객체공통속성:"+Integer.toBinaryString(objAttr))
			.append(",세로offset:"+vertOffset)
			.append(",가로offset:"+horzOffset)
			.append(",폭:"+width)
			.append(",높이:"+height)
			.append(",가로기준:"+horzRelTo.toString())
			.append(",세로기준:"+vertRelTo.toString())
			.append(",본문배치="+(wrapStyle==0?"어울림":wrapStyle==1?"자리차지":wrapStyle==2?"글뒤로":wrapStyle==3?"글앞으로":""+wrapStyle))
			.append(wrapStyle!=0?"":wrapText==0?" 양쪽":wrapText==1?" 왼쪽":wrapText==2?" 오른쪽":wrapText==3?" 큰쪽":""+wrapText)
			.append(",고유아이디="+objInstanceID)
			.append(",쪽나눔방지="+blockPageBreak)
			.append(",개체설명="+objDesc+"}=");
		return strb.toString();
	}

	@Override
	public int getSize() {
		return this.size;
	}
	
	public static enum VRelTo {
		PAPER		(0x0),
		PAGE		(0x1),
		PARA		(0x2);
		
		private int num;
	    private VRelTo(int num) { 
	    	this.num = num;
	    }
	    public static VRelTo from(int num) {
	    	for (VRelTo type: values()) {
	    		if (type.num == num)
	    			return type;
	    	}
	    	return null;
	    }
	}

	public static enum HRelTo {
		PAPER		(0x0),
		PAGE		(0x1),
		COLUMN		(0x2),
		PARA		(0x3);
		
		private int num;
	    private HRelTo(int num) { 
	    	this.num = num;
	    }
	    public static HRelTo from(int num) {
	    	for (HRelTo type: values()) {
	    		if (type.num == num)
	    			return type;
	    	}
	    	return null;
	    }
	}

   public static enum WidthRelTo {
        PAPER       (0x0),
        PAGE        (0x1),
        COLUMN      (0x2),
        PARA        (0x3),
        ABSOLUTE    (0x4);
        
        private int num;
        private WidthRelTo(int num) { 
            this.num = num;
        }
        public static WidthRelTo from(int num) {
            for (WidthRelTo type: values()) {
                if (type.num == num)
                    return type;
            }
            return null;
        }
    }
   
   public static enum HeightRelTo {
       PAPER       (0x0),
       PAGE        (0x1),
       ABSOLUTE    (0x2);
       
       private int num;
       private HeightRelTo(int num) { 
           this.num = num;
       }
       public static HeightRelTo from(int num) {
           for (HeightRelTo type: values()) {
               if (type.num == num)
                   return type;
           }
           return null;
       }
   }

   public static enum VertAlign {
       TOP			(0x0),
       CENTER		(0x1),
       BOTTOM		(0x2),
       INSIDE		(0x3),
       OUTSIDE		(0x4);
		
       private int num;
       private VertAlign(int num) { 
           this.num = num;
       }
       public static VertAlign from(int num) {
           for (VertAlign type: values()) {
               if (type.num == num)
                   return type;
           }
           return null;
       }
   }

   public static enum HorzAlign {
        LEFT        (0x0),
        CENTER      (0x1),
        RIGHT       (0x2),
        INSIDE      (0x3),
        OUTSIDE     (0x4);
        
        private int num;
        private HorzAlign(int num) { 
            this.num = num;
        }
        public static HorzAlign from(int num) {
            for (HorzAlign type: values()) {
                if (type.num == num)
                    return type;
            }
            return null;
        }
    }

}
