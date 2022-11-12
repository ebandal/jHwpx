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
package HwpDoc.HwpElement;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import HwpDoc.HwpDocInfo;
import HwpDoc.Exception.HwpParseException;
import HwpDoc.HwpElement.HwpRecordTypes.LineType2;
import HwpDoc.HwpElement.HwpRecord_TabDef.Tab;

public class HwpRecord_Numbering extends HwpRecord {
	private static final Logger log = Logger.getLogger(HwpRecord_Numbering.class.getName());
	private HwpDocInfo	parent;

	public Numbering[]	numbering = new Numbering[7];		// 문단머리정보+번호형식[1~7]
	public short		start;								// 시작번호
	public String[]		extLevelFormat	= new String[3];	// 확장 번호 형식
	public int[]		extLevelStart 	= new int[3];		// 확장 수준별 시작번호
	
	HwpRecord_Numbering(int tagNum, int level, int size) {
		super(tagNum, level, size);
	}
	
	public HwpRecord_Numbering(HwpDocInfo docInfo, int tagNum, int level, int size, byte[] buf, int off, int version) throws HwpParseException {
		this(tagNum, level, size);
		this.parent = docInfo;

		int offset = off;
		for (int i=0; i < 7; i++) {
			numbering[i] = new Numbering();
			
			int typeBits	= buf[offset+3]<<24&0xFF000000 | buf[offset+2]<<16&0x00FF0000 | buf[offset+1]<<8&0x0000FF00 | buf[offset]&0x000000FF;
			offset += 4;
		
			numbering[i].align		= (byte) ((typeBits)&0x03);
			numbering[i].useInstWidth	= (typeBits&0x40)==0x40?true:false;
			numbering[i].autoIndent		= (typeBits&0x80)==0x80?true:false;
			numbering[i].textOffsetType = (byte) ((typeBits>>>4)&0x01);
			
			numbering[i].widthAdjust	= (short) (buf[offset+1]<<8&0xFF00 | buf[offset]&0x00FF);
			offset += 2;
			numbering[i].textOffset		= (short) (buf[offset+1]<<8&0xFF00 | buf[offset]&0x00FF);
			offset += 2;
			numbering[i].charShape		= buf[offset+3]<<24&0xFF000000 | buf[offset+2]<<16&0x00FF0000 | buf[offset+1]<<8&0x0000FF00 | buf[offset]&0x000000FF;
			offset += 4;
		
			short len = (short) ((buf[offset+1]<<8&0xFF00 | buf[offset]&0x00FF)*2);
			offset += 2;
			numbering[i].numFormat 		= new String(buf, offset, len, StandardCharsets.UTF_16LE);
			offset += len;
		}

		// <numbering>의 "start" 속성에 대응
		start 	= (short) (buf[offset+1]<<8&0xFF00 | buf[offset]&0x00FF);
		offset += 2;

		if (version > 5025 && offset-off < size) {
			// 하위 <paraHead>태그의 "start" 속성에 대응
			for (int i=0; i < 7; i++) {
				numbering[i].startNumber = buf[offset+3]<<24&0xFF000000 | buf[offset+2]<<16&0x00FF0000 | buf[offset+1]<<8&0x0000FF00 | buf[offset]&0x000000FF;
				offset += 4;
			}
		}
		
		if (version > 5100 && offset-off < size) {
			for (int i=0; i < 3; i++) {
				// 내용은 알수 없으나, 8byte를 포함하고 있음.
				offset += 8;

				// 내용은 알수 없으나, 4byte를 포함하고 있음.
				offset += 4;

				// 내용을 알수 없으나, 글자수를 포함한것으로 보임.
				short len = (short) ((buf[offset+1]<<8&0xFF00 | buf[offset]&0x00FF)*2);
				offset += 2;

				// 글자수*2 만큼 건너뜀
				offset += len;
			}

			for (int i=0; i < 3; i++) {
				extLevelStart[i] = buf[offset+3]<<24&0xFF000000 | buf[offset+2]<<16&0x00FF0000 | buf[offset+1]<<8&0x0000FF00 | buf[offset]&0x000000FF;
				offset += 4;
			}
		}
		
		log.fine("                                                  "
				+"ID="+(parent.numberingList.size()+1)
				+",포맷1="+numbering[0].numFormat
					+(numbering[0].charShape!=-1?"("+((HwpRecord_CharShape)(parent.charShapeList.get(numbering[0].charShape))).fontName[0]+")":"")
				+",포맷2="+numbering[1].numFormat
					+(numbering[1].charShape!=-1?"("+((HwpRecord_CharShape)(parent.charShapeList.get(numbering[1].charShape))).fontName[0]+")":"")
				+",포맷3="+numbering[2].numFormat
					+(numbering[2].charShape!=-1?"("+((HwpRecord_CharShape)(parent.charShapeList.get(numbering[2].charShape))).fontName[0]+")":"")
				+",포맷4="+numbering[3].numFormat
					+(numbering[3].charShape!=-1?"("+((HwpRecord_CharShape)(parent.charShapeList.get(numbering[3].charShape))).fontName[0]+")":"")
				+",포맷5="+numbering[4].numFormat
					+(numbering[4].charShape!=-1?"("+((HwpRecord_CharShape)(parent.charShapeList.get(numbering[4].charShape))).fontName[0]+")":"")
				+",포맷6="+numbering[5].numFormat
					+(numbering[5].charShape!=-1?"("+((HwpRecord_CharShape)(parent.charShapeList.get(numbering[5].charShape))).fontName[0]+")":"")
				+",포맷7="+numbering[6].numFormat
					+(numbering[6].charShape!=-1?"("+((HwpRecord_CharShape)(parent.charShapeList.get(numbering[6].charShape))).fontName[0]+")":"")	
				+",시작번호="+start
				+",수준별시작번호=("+numbering[0].startNumber+","+numbering[1].startNumber+","+numbering[2].startNumber+","
								+numbering[3].startNumber+","+numbering[4].startNumber+","+numbering[5].startNumber+","+numbering[6].startNumber+")"
		 	);

		if (offset-off-size != 0) {
			dump(buf, off, size);
			throw new HwpParseException();
		}
	}
	
	public HwpRecord_Numbering(HwpDocInfo docInfo, Node node, int version) {
        super(HwpTag.HWPTAG_NUMBERING, 0, 0);
        this.parent = docInfo;
        
        dumpNode(node, 1);
        
        NamedNodeMap attributes = node.getAttributes();

        // id값은 처리하지 않는다. List<HwpRecord_CharShape>에 순차적으로 추가한다.
        // String id = attributes.getNamedItem("height").getNodeValue();
        
        start = 1;
        String numStr = attributes.getNamedItem("start").getNodeValue();
        start = (short) Integer.parseInt(numStr);
        
        NodeList nodeList = node.getChildNodes();
        for (int i=0; i<nodeList.getLength(); i++) {
            Node child = nodeList.item(i);
            
            switch(child.getNodeName()) {
            case "paraHead":
                {
                    NamedNodeMap childAttrs = child.getAttributes();
                    // level은 1수준~7수준을 의미
                    // childAttrs.getNamedItem("level").getNodeValue();
                    switch(childAttrs.getNamedItem("align").getNodeValue()) {
                    case "LEFT":
                        numbering[i].align = 0; break;
                    case "RIGHT":
                        numbering[i].align = 2; break; 
                    case "CENTER":
                        numbering[i].align = 1; break; 
                    }

                    switch(childAttrs.getNamedItem("useInstWidth").getNodeValue()) {
                    case "0":
                        numbering[i].useInstWidth = false;
                    case "1":
                        numbering[i].useInstWidth = true;
                    }
                    
                    switch(childAttrs.getNamedItem("autoIndent").getNodeValue()) {
                    case "0":
                        numbering[i].autoIndent = false;
                    case "1":
                        numbering[i].autoIndent = true;
                    }
                    
                    numStr = childAttrs.getNamedItem("widthAdjust").getNodeValue();
                    numbering[i].widthAdjust = (short)Integer.parseInt(numStr);

                    switch(childAttrs.getNamedItem("textOffsetType").getNodeValue()) {
                    case "PERCENT":
                        numbering[i].textOffsetType = 0;
                    case "HWPUNIT":
                        numbering[i].textOffsetType = 1;
                    }
                    
                    numStr = childAttrs.getNamedItem("textOffset").getNodeValue();
                    numbering[i].textOffset = (short)Integer.parseInt(numStr);
                 
                    // 67 page
                    // numbering[i].numFormat = childAttrs.getNamedItem("numFormat").getNodeValue();

                    numStr = childAttrs.getNamedItem("charPrIDRef").getNodeValue();
                    numbering[i].charShape = (short)Integer.parseInt(numStr);
                    
                    numStr = childAttrs.getNamedItem("startNumber").getNodeValue();
                    numbering[i].startNumber = (short)Integer.parseInt(numStr);
                }
            }
        }
    }

    public static class ParaHeadInfo {
		public byte			align;				    // 문단머리정보 - 문단의 정렬 종류 - 기본글꼴 존재여부
		public boolean		useInstWidth;			// 문단머리정보 - 번호 너비를 실제 인스턴스 문자열의 너비에 따를지 여부
		public boolean		autoIndent;				// 문단머리정보 - 자동 내어 쓰기 여부
		public byte			textOffsetType;			// 문단머리정보 - 수준별 본문과의 거리 종류
		public short		widthAdjust;			// 문단머리정보 - 너비 보정값
		public short		textOffset;				// 문단머리정보 - 본문과의 거리
		public int			charShape;				// 문단머리정보 - 글자 모양 아이디 참조
		public int			startNumber;            // 사용자 지정 문단 시작번호
	}
	
	public static class Numbering extends ParaHeadInfo {
		public String		numFormat;				// 번호 형식
	}

}
