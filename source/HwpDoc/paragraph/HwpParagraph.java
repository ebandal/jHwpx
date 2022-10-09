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
import java.util.List;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import HwpDoc.Exception.NotImplementedException;
import HwpDoc.HwpElement.HwpRecord;

public class HwpParagraph {
	public String	 		paraText;		// HWPTAG_PARA_TEXT
	public short			paraShapeID;	// HWPTAG_PARA_HEADER
	public short			paraStyleID;	// HWPTAG_PARA_HEADER
	public byte 			breakType;		// HWPTAG_PARA_HEADER
	public List<CharShape>	charShapes;		// HWPTAG_PARA_CHAR_SHAPE
	public LineSeg			lineSegs;		// HWPTAG_PARA_LINE_SEG
	public List<RangeTag>	rangeTags;		// HWPTAG_PARA_RANGE_TAG
	public List<Ctrl>		ctrls;

	public HwpParagraph() { }

    public static class CharShape {
		public int	start;
		public int	charShapeID;
	}

	public static class LineSeg {
		public int startPos;
		public int lineVerticalPos;
		public int lineHeight;
		public int textHeight;
		public int lineDistanceToBase;
		public int lineSpacing;
		public int columnStartPos;
		public int segmentWidth;
		public int lineTag;
		public boolean isHeadingApplied;
	}

	public HwpParagraph(Node node, int version) throws NotImplementedException {

        NamedNodeMap attributes = node.getAttributes();
        
        // id값은 처리하지 않는다. List<HwpRecord_Style>에 순차적으로 추가한다.
        // String id = attributes.getNamedItem("id").getNodeValue();

        String numStr = attributes.getNamedItem("paraPrIDRef").getNodeValue();
        paraShapeID = (short) Integer.parseInt(numStr);

        numStr = attributes.getNamedItem("styleIDRef").getNodeValue();
        paraStyleID = (short) Integer.parseInt(numStr);

        switch(attributes.getNamedItem("pageBreak").getNodeValue()) {
        case "0":
            breakType &= 0b11111011;   break;      // 0:구역나누기, 2:다단나누기, 4:쪽 나누기, 8:단 나누기
        case "1":
            breakType |= 0b00000100;   break;
        }

        switch(attributes.getNamedItem("columnBreak").getNodeValue()) {
        case "0":
            breakType &= 0b11110111;   break;      // 0:구역나누기, 2:다단나누기, 4:쪽 나누기, 8:단 나누기
        case "1":
            breakType |= 0b00001000;   break;
        }

        // attributes.getNamedItem("merged").getNodeValue();
        // attributes.getNamedItem("paraTcId").getNodeValue();

        NodeList nodeList = node.getChildNodes();
        for (int i=0; i<nodeList.getLength(); i++) {
            Node child = nodeList.item(i);
            
            switch(child.getNodeName()) {
            case "hp:run":
                {
                    NamedNodeMap childAttrs = child.getAttributes();
                    charShapes = new ArrayList<CharShape>();
                    CharShape charShape = new CharShape();
                    
                    numStr = childAttrs.getNamedItem("charPrIDRef").getNodeValue();
                    charShape.charShapeID = Integer.parseInt(numStr);
                    if (childAttrs.getNamedItem("charTcId")!=null) {
                        numStr = childAttrs.getNamedItem("charTcId").getNodeValue();
                        charShape.start = Integer.parseInt(numStr);
                    }
                    charShapes.add(charShape);
                    
                    NodeList childNodeList = child.getChildNodes();
                    for (int j=0; j<childNodeList.getLength(); j++) {
                        Node grandChild = childNodeList.item(j);
                        recursive_HwpParagraph(grandChild);
                    }
                }
                break;
            case "hp:linesegarray":
                break;
            }
        }
	}
	
	private void recursive_HwpParagraph(Node node) throws NotImplementedException {

        if (ctrls == null) {
            ctrls = new ArrayList<Ctrl>();
        }

        String numStr;
	    Ctrl ctrl;
	    
	    switch(node.getNodeName()) {
	    case "hp:secPr":
    	    {
    	        ctrl = new Ctrl_SectionDef("dces", node);
    	        ctrls.add(ctrl);
    	    }
    	    break;
	    case "hp:ctrl":
    	    {
                NodeList nodeList = node.getChildNodes();
                for (int j=0; j<nodeList.getLength(); j++) {
                    Node child = nodeList.item(j);
                    ctrl = Ctrl.getCtrl(child);
                    ctrls.add(ctrl);
                }
    	    }
    	    break;
	    case "hp:t":
            {
                HwpRecord.dumpNode(node, 1);
                NamedNodeMap attrs = node.getAttributes();


                if (attrs.getNamedItem("charPrIDRef")!=null) {
                    if (charShapes==null) {
                        charShapes = new ArrayList<>();
                    }
                    CharShape charShape = new CharShape();
                    numStr = attrs.getNamedItem("charPrIDRef").getNodeValue();
                    charShape.charShapeID = Integer.parseInt(numStr);
                    charShapes.add(charShape);
                }
                
                NodeList nodeList = node.getChildNodes();
                for (int j=0; j<nodeList.getLength(); j++) {
                    Node child = nodeList.item(j);
                    switch(child.getNodeName()) {
                    case "#text":
                        paraText = child.getNodeValue();    
                        break;
                    case "markpenBegin":    // 134 page
                    case "markpenEnd":
                    case "titleMark":
                    case "tab":
                    case "lineBreak":
                    case "hyphen":
                    case "nbSpace":
                    case "fwSpace":
                    case "insertBegin":
                    case "insertEnd":
                    case "deleteBegin":
                    case "deleteEnd":
                        throw new NotImplementedException("hp:t");
                    }
                }
            }
            break;
	    case "hp:tbl":
        {
            HwpRecord.dumpNode(node, 1);
            // [[hp:sz: null], [hp:pos: null], [hp:outMargin: null], [hp:inMargin: null], [hp:tr: null], [hp:tr: null], [hp:tr: null], [hp:tr: null], [hp:tr: null], [hp:tr: null], [hp:tr: null], [hp:tr: null], [hp:tr: null], [hp:tr: null], [hp:tr: null], [hp:tr: null]]
        }
        break;
	    case "hp:pic":
        {
            HwpRecord.dumpNode(node, 1);
        }
        break;
	    case "hp:container":
        {
            HwpRecord.dumpNode(node, 1);
        }
        break;
	    case "hp:ole":
        {
            HwpRecord.dumpNode(node, 1);
        }
        break;
	    case "hp:equation":
        {
            HwpRecord.dumpNode(node, 1);
        }
        break;
	    case "hp:line":
        {
            HwpRecord.dumpNode(node, 1);
        }
        break;
	    case "hp:rect":
        {
            HwpRecord.dumpNode(node, 1);
            // [[hp:offset: null], [hp:orgSz: null], [hp:curSz: null], [hp:flip: null], [hp:rotationInfo: null], [hp:renderingInfo: null], [hp:lineShape: null], [hc:fillBrush: null], [hp:shadow: null], [hp:drawText: null], [hc:pt0: null], [hc:pt1: null], [hc:pt2: null], [hc:pt3: null], [hp:sz: null], [hp:pos: null], [hp:outMargin: null]]
        }
        break;
	    case "hp:ellipse":
        {
            HwpRecord.dumpNode(node, 1);
        }
        break;
	    case "hp:arc":
        {
            HwpRecord.dumpNode(node, 1);
        }
        break;
	    case "hp:polygon":
        {
            HwpRecord.dumpNode(node, 1);
        }
        break;
	    case "hp:curve":
        {
            HwpRecord.dumpNode(node, 1);
        }
        break;
	    case "hp:connectLine":
        {
            HwpRecord.dumpNode(node, 1);
        }
        break;
	    case "hp:textart":
        {
            HwpRecord.dumpNode(node, 1);
        }
        break;
	    case "hp:compose":
        {
            HwpRecord.dumpNode(node, 1);
        }
        break;
	    case "hp:dutmal":
        {
            HwpRecord.dumpNode(node, 1);
        }
        break;
	    case "hp:btn":
        {
            HwpRecord.dumpNode(node, 1);
        }
        break;
	    case "hp:radioBtn":
        {
            HwpRecord.dumpNode(node, 1);
        }
        break;
	    case "hp:checkBtn":
        {
            HwpRecord.dumpNode(node, 1);
        }
        break;
	    case "hp:comboBox":
        {
            HwpRecord.dumpNode(node, 1);
        }
        break;
	    case "hp:edit":
        {
            HwpRecord.dumpNode(node, 1);
        }
        break;
	    case "hp:listBox":
        {
            HwpRecord.dumpNode(node, 1);
        }
        break;
	    case "hp:scrollBar":
        {
            HwpRecord.dumpNode(node, 1);
        }
        break;
	    case "hp:video":
        {
            HwpRecord.dumpNode(node, 1);
        }
        break;
        default:
            throw new NotImplementedException("recusive_HwpPargraph");
	    }
	    
	}



}
