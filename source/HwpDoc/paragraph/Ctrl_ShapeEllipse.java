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

public class Ctrl_ShapeEllipse extends Ctrl_GeneralShape {
	private static final Logger log = Logger.getLogger(Ctrl_ShapeEllipse.class.getName());
	private int size;
	
	// 타원 개체 속성
	public boolean  intervalDirty;     // 속성 (표 97참조)
	public boolean  hasArcProperty;
	public ArcType  arcType;
	public int		centerX;	// 중심 좌표의 X값
	public int		centerY;	// 중심 좌표의 Y값
	public int		axixX1;		// 제1축 X 좌표의 값
	public int		axixY1;		// 제1축 Y 좌표의 값
	public int		axixX2;		// 제2축 X 좌표의 값
	public int		axixY2;		// 제2축 Y 좌표의 값
	public int 		startX1;
	public int 		startY1;
	public int		endX1;
	public int		endY1;
	public int 		startX2;
	public int 		startY2;
	public int		endX2;
	public int		endY2;
	
	public Ctrl_ShapeEllipse(String ctrlId, int size, byte[] buf, int off, int version) {
		super(ctrlId, size, buf, off, version);
		this.size = offset-off;

		log.fine("                                                  " + toString());
	}
	
	public Ctrl_ShapeEllipse(Ctrl_GeneralShape shape) {
		super(shape);
		
		this.size = shape.getSize();
	}

	public Ctrl_ShapeEllipse(String ctrlId, Node node, int version) throws NotImplementedException {
        super(ctrlId, node, version);
        
        NamedNodeMap attributes = node.getAttributes();
        // 호로 바뀌었을때 interval을 다시 계산해야 할 필요가 있는지 여부
        switch(attributes.getNamedItem("intervalDirty").getNodeValue()) {   
        case "0":
            intervalDirty = false;      break;
        case "1":
            intervalDirty = true;       break;
        };
        // 호로 바뀌었는지 여부
        switch(attributes.getNamedItem("hasArcProperty").getNodeValue()) {
        case "0":
            intervalDirty = false;      break;
        case "1":
            intervalDirty = true;       break;
        };
        // 호의 종류
        arcType = ArcType.valueOf(attributes.getNamedItem("arcType").getNodeValue());
        
        String numStr;
        
        NodeList nodeList = node.getChildNodes();
        for (int i=0; i<nodeList.getLength(); i++) {
            Node child = nodeList.item(i);
            NamedNodeMap childAttrs = child.getAttributes();
            switch(child.getNodeName()) {
            case "hp:center":   // 중심좌표
                numStr = childAttrs.getNamedItem("x").getNodeValue();
                centerX = Integer.parseInt(numStr);
                numStr = childAttrs.getNamedItem("y").getNodeValue();
                centerY = Integer.parseInt(numStr);
                break;
            case "hp:ax1":  // 제1축 좌표
                numStr = childAttrs.getNamedItem("x").getNodeValue();
                axixX1 = Integer.parseInt(numStr);
                numStr = childAttrs.getNamedItem("y").getNodeValue();
                axixY1 = Integer.parseInt(numStr);
                break;
            case "hp:ax2":  // 제2축 좌표
                numStr = childAttrs.getNamedItem("x").getNodeValue();
                axixX2 = Integer.parseInt(numStr);
                numStr = childAttrs.getNamedItem("y").getNodeValue();
                axixY2 = Integer.parseInt(numStr);
                break;
            case "hp:start1":  // 시작지점 1 좌표
                numStr = childAttrs.getNamedItem("x").getNodeValue();
                startX1 = Integer.parseInt(numStr);
                numStr = childAttrs.getNamedItem("y").getNodeValue();
                startY1 = Integer.parseInt(numStr);
                break;
            case "hp:start2":  // 시작지점 2 좌표
                numStr = childAttrs.getNamedItem("x").getNodeValue();
                startX2 = Integer.parseInt(numStr);
                numStr = childAttrs.getNamedItem("y").getNodeValue();
                startY2 = Integer.parseInt(numStr);
                break;
            case "hp:end1":  // 끝지점 1좌표
                numStr = childAttrs.getNamedItem("x").getNodeValue();
                endX1 = Integer.parseInt(numStr);
                numStr = childAttrs.getNamedItem("y").getNodeValue();
                endY1 = Integer.parseInt(numStr);
                break;
            case "hp:end2":  // 끝지점 2좌표
                numStr = childAttrs.getNamedItem("x").getNodeValue();
                endX2 = Integer.parseInt(numStr);
                numStr = childAttrs.getNamedItem("y").getNodeValue();
                endY2 = Integer.parseInt(numStr);
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
	
   public static enum ArcType {
        NORMAL      (0x0),
        PIE         (0x1),
        CHORD       (0x2);
        
        private int num;
        private ArcType(int num) { 
            this.num = num;
        }
        public static ArcType from(int num) {
            for (ArcType type: values()) {
                if (type.num == num)
                    return type;
            }
            return null;
        }
    }
}
