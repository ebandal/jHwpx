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
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import HwpDoc.Exception.NotImplementedException;
import HwpDoc.HwpElement.HwpRecord;
import HwpDoc.paragraph.Ctrl_Character.CtrlCharType;
import HwpDoc.paragraph.Ctrl_Common.VertAlign;
import HwpDoc.paragraph.Ctrl_Table.CellZone;

public class TblCell {
	private static final Logger log = Logger.getLogger(TblCell.class.getName());

	private int 	size;
	
	public short	colAddr;			// 셀 주소(Column, 맨 왼쪽 셀이 0부터 시작하여 1씩 증가)
	public short	rowAddr;			// 셀 주소(Row, 맨 위쪽 셀이 0부터 시작하여 1씩 증가)
	public short	colSpan;			// 열의 병합 갯수
	public short	rowSpan;			// 행의 병합 갯수
	public int		width;				// 셀의 폭
	public int		height;				// 셀의 높이
	public short[]	margin;				// 셀 4방향 여백
	public short	borderFill;			// 테두리/배경 아이디
	public List<CellParagraph>	paras;	// 내용  HWPTAG_PARA_TEXT + HWPTAG_PARA_CHAR_SHAPE + HWP_PARA_LINE_SEG
	public VertAlign   verAlign;		// LIST_HEADER 에 (문단갯수,텍스트방향,문단줄바꿈,세로정렬) 포함되어 있다.
	
	public TblCell(int size, byte[] buf, int off, int version) {
		int offset = off;
		
		// 앞 2byte는 무시한다.  해석불가.
		offset += 2;
		
		colAddr	= (short) (buf[offset+1]<<8&0xFF00 | buf[offset]&0x00FF);
		offset += 2;
		rowAddr	= (short) (buf[offset+1]<<8&0xFF00 | buf[offset]&0x00FF);
		offset += 2;
		colSpan	= (short) (buf[offset+1]<<8&0xFF00 | buf[offset]&0x00FF);
		offset += 2;
		rowSpan	= (short) (buf[offset+1]<<8&0xFF00 | buf[offset]&0x00FF);
		offset += 2;
		width	= buf[offset+3]<<24&0xFF000000 | buf[offset+2]<<16&0x00FF0000 | buf[offset+1]<<8&0x0000FF00 | buf[offset]&0x000000FF;
		offset += 4;
		height	= buf[offset+3]<<24&0xFF000000 | buf[offset+2]<<16&0x00FF0000 | buf[offset+1]<<8&0x0000FF00 | buf[offset]&0x000000FF;
		offset += 4;
		
		margin = new short[4];
		for (int i=0;i<4;i++) {
			margin[i]	= (short) (buf[offset+1]<<8&0xFF00 | buf[offset]&0x00FF);
			offset += 2;
		}
		borderFill	= (short) (buf[offset+1]<<8&0xFF00 | buf[offset]&0x00FF);
		offset += 2;
		
		log.fine("                                                  " + "[CELL]" + toString());
		
		// 41byte중 28byte만 해석 가능.  내용을 모르므로 41byte 모두 읽은것 처럼 size 조작한다.
		this.size = size;
	}
	
	public TblCell(Node node, int version) throws NotImplementedException {
        NamedNodeMap attrs = node.getAttributes();
        
        // attrs.getNamedItem("name").getNodeValue();
        // attrs.getNamedItem("header").getNodeValue();
        // attrs.getNamedItem("hasMargin").getNodeValue();
        // attrs.getNamedItem("protect").getNodeValue();
        // attrs.getNamedItem("editable").getNodeValue();
        // attrs.getNamedItem("dirty").getNodeValue();
        String numStr = attrs.getNamedItem("borderFillIDRef").getNodeValue();
        borderFill = (short)Integer.parseInt(numStr);
        
        NodeList nodeList = node.getChildNodes();
        for (int i=0; i<nodeList.getLength(); i++) {
            Node child = nodeList.item(i);
            
            switch(child.getNodeName()) {
            case "hp:cellAddr":
                {
                    NamedNodeMap childAttrs = child.getAttributes();
                    numStr = childAttrs.getNamedItem("colAddr").getNodeValue();
                    colAddr = (short) Integer.parseInt(numStr);
                    numStr = childAttrs.getNamedItem("rowAddr").getNodeValue();
                    rowAddr = (short) Integer.parseInt(numStr);
                }
                break;
            case "hp:cellSpan":
                {
                    NamedNodeMap childAttrs = child.getAttributes();
                    numStr = childAttrs.getNamedItem("colSpan").getNodeValue();
                    colSpan = (short) Integer.parseInt(numStr);
                    numStr = childAttrs.getNamedItem("rowSpan").getNodeValue();
                    rowSpan = (short) Integer.parseInt(numStr);
                }
                break;
            case "hp:cellSz":
                {
                    NamedNodeMap childAttrs = child.getAttributes();
                    numStr = childAttrs.getNamedItem("width").getNodeValue();
                    width = Integer.parseInt(numStr);
                    numStr = childAttrs.getNamedItem("height").getNodeValue();
                    height = Integer.parseInt(numStr);
                }
                break;
            case "hp:cellMargin":
                {
                    if (margin==null) {
                        margin = new short[4];
                    }
                    NamedNodeMap childAttrs = child.getAttributes();
                    numStr = childAttrs.getNamedItem("left").getNodeValue();
                    margin[0] = (short) Integer.parseInt(numStr);
                    numStr = childAttrs.getNamedItem("right").getNodeValue();
                    margin[1] = (short) Integer.parseInt(numStr);
                    numStr = childAttrs.getNamedItem("top").getNodeValue();
                    margin[2] = (short) Integer.parseInt(numStr);
                    numStr = childAttrs.getNamedItem("bottom").getNodeValue();
                    margin[3] = (short) Integer.parseInt(numStr);
                }
                break;
            case "hp:subList":
                {
                    NamedNodeMap childAttrs = child.getAttributes();
                    // childAttrs.getNamedItem("id").getNodeValue();
                    // childAttrs.getNamedItem("textDirection").getNodeValue();
                    // childAttrs.getNamedItem("lineWrap").getNodeValue();
                    verAlign = VertAlign.valueOf(childAttrs.getNamedItem("vertAlign").getNodeValue());
                    // childAttrs.getNamedItem("linkListIDRef").getNodeValue();
                    // childAttrs.getNamedItem("linkListNextIDRef").getNodeValue();
                    
                    if (paras==null) {
                        paras = new ArrayList<CellParagraph>();
                    }
                    NodeList childNodeList = child.getChildNodes();
                    for (int j=0; j<childNodeList.getLength(); j++) {
                        Node grandChild = childNodeList.item(j);
                        Ctrl lastCtrl = null;
                        switch(grandChild.getNodeName()) {
                        case "hp:p":
                            // HwpRecord.dumpNode(grandChild, 1);
                            CellParagraph cellP = new CellParagraph(grandChild, version);
                            paras.add(cellP);
                            lastCtrl = (cellP.p==null ? null : cellP.p.getLast());
                            break;
                        default:
                            throw new NotImplementedException("TblCell");
                        }
                        
                        // ParaBreak를 subList 중간에 하나씩 강제로 넣는다. Paragraph 단위로 다음줄에 써지도록
                        if (lastCtrl != null && lastCtrl instanceof ParaText) {
                            CellParagraph breakP = new CellParagraph(grandChild, version);
                            if (breakP.p!=null) {
                                breakP.p.clear();
                            } else {
                                breakP.p = new LinkedList<Ctrl>();
                            }
                            breakP.p.add(new Ctrl_Character("  _", CtrlCharType.PARAGRAPH_BREAK));
                            paras.add(breakP);
                        }
                    }
                }
                break;
            default:
                throw new NotImplementedException("TblCell");
            }
        }

    }

    public String toString() {
		StringBuffer strb = new StringBuffer();
		strb.append("ColAddr="+colAddr)
			.append(",RowAddr="+rowAddr)
			.append(",ColSpan="+colSpan)
			.append(",RowSpan="+rowSpan)
			.append(",폭="+width)
			.append(",높이="+height)
			.append(",테두리/배경 아이디="+borderFill);
		if (margin!=null) {
			strb.append(",여백=");
			for (int i=0; i<margin.length;i++) {
				strb.append(margin[i]+":");
			}
		}
		strb.append(",FillID="+borderFill);
		return strb.toString();
	}
	
	public int getSize() {
		return size;
	}
}
