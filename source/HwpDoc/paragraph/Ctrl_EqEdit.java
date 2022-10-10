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
import HwpDoc.HwpElement.HwpRecord_ShapePicture.Neon;
import HwpDoc.HwpElement.HwpRecord_ShapePicture.PicEffectType;
import HwpDoc.HwpElement.HwpRecord_ShapePicture.Reflect;
import HwpDoc.HwpElement.HwpRecord_ShapePicture.Shadow;
import HwpDoc.HwpElement.HwpRecord_ShapePicture.SoftEdge;

public class Ctrl_EqEdit extends Ctrl_GeneralShape {
	private static final Logger log = Logger.getLogger(Ctrl_EqEdit.class.getName());
	private int size;
	
	public int 			attr;			// 속성. 스크립트가 차지하는 범위. 첫 비트가 켜져있으면 줄단위, 꺼져있으면 글자 단위
	// public short		len;			// 스크립트 길이
	public String		eqn;			// 한글 수식 스크립트
	public int			charSize;		// 수식 글자 크기
	public int			color;			// 글자 색상
	public int			baseline;		// baseline
	public String		version;		// 수식 버전 정보
	public String		font;			// 수식 폰트 이름
	
	public Ctrl_EqEdit(String ctrlId, int size, byte[] buf, int off, int version) {
		super(ctrlId, size, buf, off, version);
		this.size = offset-off;

		log.fine("                                                  " + toString());
	}
	
	public Ctrl_EqEdit(Ctrl_GeneralShape shape) {
		super(shape);
		
		this.size = shape.getSize();
	}

	public Ctrl_EqEdit(String ctrlId, Node node, int ver) throws NotImplementedException {
        super(ctrlId, node, ver);
        
        NamedNodeMap attributes = node.getAttributes();
        version = attributes.getNamedItem("version").getNodeValue();
        
        String numStr = attributes.getNamedItem("baseLine").getNodeValue(); // 수식이 그려질 기본 선
        baseline = Integer.parseInt(numStr);
        
        numStr = attributes.getNamedItem("textColor").getNodeValue(); // 수식 글자 색
        if (!numStr.equals("NONE")) {
            numStr = numStr.replaceAll("#",  "");
            color = Integer.parseInt(numStr, 16);
        }
        
        numStr = attributes.getNamedItem("baseUnit").getNodeValue();  // 수식의 글자 크기
        charSize = Integer.parseInt(numStr);
        
        switch(attributes.getNamedItem("lineMode").getNodeValue()) {  // 수식이 차지하는 범위
        case "0":
            break;
        case "LINE":
        case "CHAR":
        default:
            throw new NotImplementedException("EqEdit");
        }
        
        font = attributes.getNamedItem("font").getNodeValue();  // 수식 폰트
        
        NodeList nodeList = node.getChildNodes();
        for (int i=0; i<nodeList.getLength(); i++) {
            Node child = nodeList.item(i);
            switch(child.getNodeName()) {
            case "hp:script":    // 수식내용
                eqn = child.getNodeValue();
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
