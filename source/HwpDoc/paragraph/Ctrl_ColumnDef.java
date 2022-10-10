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

import java.util.logging.Logger;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import HwpDoc.Exception.NotImplementedException;
import HwpDoc.HwpElement.HwpRecordTypes.LineType2;

public class Ctrl_ColumnDef extends Ctrl {
	private static final Logger log = Logger.getLogger(Ctrl_ColumnDef.class.getName());

	private int size;
	
	public int         attr;
	public short       colCount;
	public boolean     sameSz;
	public short       sameGap;
	public short[]     colSzWidths;
	public short[]     colSzGaps;
	public LineType2   colLineType;
	public byte        colLineWidth;
	public int         colLineColor;
	
	public Ctrl_ColumnDef(String ctrlId, int size, byte[] buf, int off, int version) {
		super(ctrlId);
		
		int offset = off;
		// 속성의 bit 0-15(표 139참조)
		short attrLowBits	= (short) (buf[offset+1]<<8&0xFF00 | buf[offset]&0x00FF);
		offset += 2;
		// 단 사이 간격
		sameGap				= (short) (buf[offset+1]<<8&0xFF00 | buf[offset]&0x00FF);
		offset += 2;
		
		colCount          	= (short) (attrLowBits>>2 & 0xFF);
		sameSz              = (attrLowBits>>12 & 0x1) == 0x1; 
		// 단 너비가 동일하지 않으면, 단의 개수만큼 단의 폭
		if (!sameSz) {
		    colSzWidths = new short[colCount];
		    colSzGaps = new short[colCount-1];
			for(int i=0;i<colCount;i++) {
			    colSzWidths[i]		= (short) (buf[offset+1]<<8&0xFF00 | buf[offset]&0x00FF);
				offset += 2;
				if (i<colCount-1) {
				    colSzGaps[i]   = (short) (buf[offset+1]<<8&0xFF00 | buf[offset]&0x00FF);
	                offset += 2;
				}
			}
		}
		// 속성의 bit 16-32(표 139 참조)
		short attrHighBits	= (short) (buf[offset+1]<<8&0xFF00 | buf[offset]&0x00FF);
		offset += 2;
		// 단 구분선 종류(테두리/배경이 테두리 선 종류 참조)
		colLineType		= LineType2.from(buf[offset++]);
		// 단 구분선 굵기(테두리/배경이 테두리 선 굵기 참조)
		colLineWidth		= buf[offset++];
		// 단 구분선 굵기(테두리/배경이 테두리 선 굵기 참조)
		colLineColor		= buf[offset+3]<<24&0xFF000000 | buf[offset+2]<<16&0x00FF0000 | buf[offset+1]<<8&0x0000FF00 | buf[offset]&0x000000FF;
		offset += 4;
		attr 				= attrHighBits<<16 | attrLowBits;
		
		log.fine("                                                  " + toString());

		this.size = offset-off;
		
		log.finest(toString());
	}
	
	public Ctrl_ColumnDef(String ctrlId, Node node, int version) throws NotImplementedException {
        super(ctrlId);
        
        NamedNodeMap attributes = node.getAttributes();
        
        switch(attributes.getNamedItem("type").getNodeValue()) {
        case "NEWSPAPER":
        case "BALANCED_NEWSPAPER":
        case "PARALLEL":
            break;
        }
        
        switch(attributes.getNamedItem("layout").getNodeValue()) {
        case "LEFT":
        case "RIGHT":
        case "MIRROR":
            break;
        }

        String numStr = attributes.getNamedItem("colCount").getNodeValue();
        colCount = (short) Integer.parseInt(numStr);

        switch(attributes.getNamedItem("sameSz").getNodeValue()) {
        case "0":
            sameSz = false;    break;
        case "1":
            sameSz = true;     break;
        default:
            throw new NotImplementedException("Ctrl_ColumnDef");
        }

        // attributes.getNamedItem("sameGap").getNodeValue()
        if (!sameSz) {
            colSzWidths = new short[colCount];
            colSzGaps = new short[colCount-1];
        }
        
        int colSzIdx = 0;
        NodeList nodeList = node.getChildNodes();
        for (int i=0; i<nodeList.getLength(); i++) {
            Node child = nodeList.item(i);
            
            switch(child.getNodeName()) {
            case "colLine":
                {
                    NamedNodeMap childAttrs = child.getAttributes();
                    colLineType = LineType2.valueOf(childAttrs.getNamedItem("type").getNodeValue());

                    switch(childAttrs.getNamedItem("width").getNodeValue()) {
                    case "0.1":
                    case "0.1 mm":
                        colLineWidth = 0; break;
                    case "0.12":
                    case "0.12 mm":
                        colLineWidth = 1; break;
                    case "0.15":
                    case "0.15 mm":
                        colLineWidth = 2; break;
                    case "0.2":
                    case "0.2 mm":
                        colLineWidth = 3; break;
                    case "0.25":
                    case "0.25 mm":
                        colLineWidth = 4; break;
                    case "0.3":
                    case "0.3 mm":
                        colLineWidth = 5; break;
                    case "0.4":
                    case "0.4 mm":
                        colLineWidth = 6; break;
                    case "0.5":
                    case "0.5 mm":
                        colLineWidth = 7; break;
                    case "0.6":
                    case "0.6 mm":
                        colLineWidth = 8; break;
                    case "0.7":
                    case "0.7 mm":
                        colLineWidth = 9; break;
                    case "1.0":
                    case "1.0 mm":
                        colLineWidth = 10; break;
                    case "1.5":
                    case "1.5 mm":
                        colLineWidth = 11; break;
                    case "2.0":
                    case "2.0 mm":
                        colLineWidth = 12; break;
                    case "3.0":
                    case "3.0 mm":
                        colLineWidth = 13; break;
                    case "4.0":
                    case "4.0 mm":
                        colLineWidth = 14; break;
                    case "5.0":
                    case "5.0 mm":
                        colLineWidth = 15; break;
                    }

                    colLineColor = 0x000000;
                    numStr = childAttrs.getNamedItem("color").getNodeValue();
                    if (!numStr.equals("NONE")) {
                        numStr = numStr.replaceAll("#",  "");
                        colLineColor = Integer.parseInt(numStr, 16);
                    }
                }
                break;
            case "colSz":
                {
                    NamedNodeMap childAttrs = child.getAttributes();
                    
                    numStr = childAttrs.getNamedItem("width").getNodeValue();
                    colSzWidths[colSzIdx] = (short) Integer.parseInt(numStr);

                    numStr = childAttrs.getNamedItem("gap").getNodeValue();
                    colSzGaps[colSzIdx] = (short) Integer.parseInt(numStr);
                    
                    colSzIdx++;
                }
                break;
            }
        }
    }

    public String toString() {
		StringBuffer strb = new StringBuffer();
		strb.append("CTRL("+ctrlId+")")
			.append("=속성:"+Integer.toBinaryString(attr))
			.append(",단개수:"+colCount)
			.append(",단간격:"+sameGap);
		if (colSzWidths!=null) {
			for(int i=0;i<colSzWidths.length;i++) {
				strb.append(",너비"+i+":"+colSzWidths[i]);
			}
		}
		strb.append(",구분선종류:"+colLineType)
			.append(",구분선굵기:"+colLineWidth)
			.append(",구분선색상:"+colLineColor);
		return strb.toString();
	}

	public int getSize() {
		return size;
	}
}
