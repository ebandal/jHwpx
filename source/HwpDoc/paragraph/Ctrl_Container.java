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
import java.util.logging.Logger;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Ctrl_Container extends Ctrl_GeneralShape {
	private static final Logger log = Logger.getLogger(Ctrl_Container.class.getName());
	private int size;

	public short					nElement;	// 개체의 개수
	public List<String>				ctrlIdList;	// 개체의 컨트롤 ID array
	public List<Ctrl_GeneralShape>	list;		// 개체 속성 x 묶음 개체의 갯수
	
	public Ctrl_Container(String ctrlId, int size, byte[] buf, int off, int version) {
		super(ctrlId, size, buf, off, version);
		this.size = offset-off;
	}
	
	public Ctrl_Container(Ctrl_GeneralShape shape) {
		super(shape);
		
		this.size = shape.getSize();
	}

    public Ctrl_Container(String ctrlId, Node node, int version) {
        super(ctrlId, node, version);
        
        NodeList nodeList = node.getChildNodes();
        nElement = (short) nodeList.getLength();
        if (nElement > 0) {
            if (list==null) {
                list = new ArrayList<Ctrl_GeneralShape>();
            }
        }
        for (int i=0; i<nodeList.getLength(); i++) {
            Node child = nodeList.item(i);
            
            Ctrl_GeneralShape ctrl;
            switch(child.getNodeName()) {
            case "hp:container":    // 컨테이너 객체
                ctrl = new Ctrl_Container("noc$", node, version);
                list.add(ctrl);
                break;
            case "hp:line":     // 그리기 객체 - 선
                ctrl = new Ctrl_ShapeLine("nil$", node, version);
                list.add(ctrl);
                break;
            case "hp:rect":
                ctrl = new Ctrl_ShapeRect("cer$", node, version);
                list.add(ctrl);
                break;
            case "hp:ellipse":
                ctrl = new Ctrl_ShapeEllipse("lle$", node, version);
                list.add(ctrl);
                break;
            case "hp:arc":
                ctrl = new Ctrl_ShapeArc("cra$", node, version);
                list.add(ctrl);
                break;
            case "hp:polygon":
                ctrl = new Ctrl_ShapePolygon("lop$", node, version);
                list.add(ctrl);
                break;
            case "hp:curve":
                ctrl = new Ctrl_ShapeCurve("ruc$", node, version);
                list.add(ctrl);
                break;
            case "hp:connectLine":
                ctrl = new Ctrl_ShapeLine("loc$", node, version);
                list.add(ctrl);
                break;
            case "hp:pic":
                ctrl = new Ctrl_ShapePic("cip$", node, version);
                list.add(ctrl);
                break;
            case "hp:ole":
                ctrl = new Ctrl_ShapeOle("elo$", node, version);
                list.add(ctrl);
                break;
            }
        }
    }

}
