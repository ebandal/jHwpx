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

public class Ctrl_ShapeRect extends Ctrl_GeneralShape {
	private static final Logger log = Logger.getLogger(Ctrl_ShapeRect.class.getName());
	private int size;	
	
	// 사각형 개체 속성
	public byte		curv;	// 사각형 모서리 곡률(%)  직각은 0, 둥근모양 20, 반원 50, 그외 % 단위
	public Point[]	points;	// 사각형의 좌표 x, 사각형의 좌표 y
	
	public Ctrl_ShapeRect(String ctrlId, int size, byte[] buf, int off, int version) {
		super(ctrlId, size, buf, off, version);
		this.size = offset-off;

		log.fine("                                                  " + toString());
	}
	
	public Ctrl_ShapeRect(Ctrl_GeneralShape shape) {
		super(shape);
		
		this.size = shape.getSize();
	}
	
	public Ctrl_ShapeRect(String ctrlId, Node node, int version) throws NotImplementedException {
        super(ctrlId, node, version);
        
        NamedNodeMap attributes = node.getAttributes();
        String numStr = attributes.getNamedItem("ratio").getNodeValue();
        curv = (byte) Integer.parseInt(numStr);
        
        NodeList nodeList = node.getChildNodes();
        if (nodeList!=null && nodeList.getLength()>0) {
            points = new Point[4];
        }
        for (int i=0; i<nodeList.getLength(); i++) {
            Node child = nodeList.item(i);
            NamedNodeMap childAttrs = child.getAttributes();
            switch(child.getNodeName()) {
            case "hp:pt0":  // 첫번째 좌표
                points[0] = new Point();
                numStr = childAttrs.getNamedItem("x").getNodeValue();
                points[0].x = Integer.parseInt(numStr);
                numStr = childAttrs.getNamedItem("y").getNodeValue();
                points[0].y = Integer.parseInt(numStr);
                break;
            case "hp:pt1":  // 두번째 좌표
                points[1] = new Point();
                numStr = childAttrs.getNamedItem("x").getNodeValue();
                points[1].x = Integer.parseInt(numStr);
                numStr = childAttrs.getNamedItem("y").getNodeValue();
                points[1].y = Integer.parseInt(numStr);
                break;
            case "hp:pt2":  // 세번째 좌표
                points[2] = new Point();
                numStr = childAttrs.getNamedItem("x").getNodeValue();
                points[2].x = Integer.parseInt(numStr);
                numStr = childAttrs.getNamedItem("y").getNodeValue();
                points[2].y = Integer.parseInt(numStr);
                break;
            case "hp:pt3":  // 네번째 좌표
                points[3] = new Point();
                numStr = childAttrs.getNamedItem("x").getNodeValue();
                points[3].x = Integer.parseInt(numStr);
                numStr = childAttrs.getNamedItem("y").getNodeValue();
                points[3].y = Integer.parseInt(numStr);
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

	@Override
	public int getSize() {
		return size;
	}

}
