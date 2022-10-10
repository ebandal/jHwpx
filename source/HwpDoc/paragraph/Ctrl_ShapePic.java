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

import HwpDoc.Exception.NotImplementedException;
import HwpDoc.HwpElement.HwpRecord_ShapePicture.Neon;
import HwpDoc.HwpElement.HwpRecord_ShapePicture.PicEffect;
import HwpDoc.HwpElement.HwpRecord_ShapePicture.PicEffectType;
import HwpDoc.HwpElement.HwpRecord_ShapePicture.Reflect;
import HwpDoc.HwpElement.HwpRecord_ShapePicture.Shadow;
import HwpDoc.HwpElement.HwpRecord_ShapePicture.SoftEdge;
import HwpDoc.paragraph.Ctrl_Table.CellZone;

public class Ctrl_ShapePic extends Ctrl_GeneralShape {
	private static final Logger log = Logger.getLogger(Ctrl_ShapePic.class.getName());
	private int size;
	
	public int 			borderColor;	// 테두리 색
	public int			borderThick;	// 테두리 두께
	public int			borderAttr;		// 테두리 속성
	public Point[]		borderPoints;	// 이미지의 테두리 사각형의 x,y 좌표(최초 그림 삽입 시 크기)
	public int			cropLeft;		// 자르기 한 후 사각형의 left
	public int			cropTop;		// 자르기 한 후 사각형의 top
	public int			cropRight;		// 자르기 한 후 사각형의 right
	public int			cropBottom;		// 자르기 한 후 사각형의 bottom
	public short[]		innerSpaces;	// 안쪽여백(왼쪽여백,오른쪽여백,위쪽여백,아래쪽여백) default:141(표) or 0(그림)
	public byte			bright;			// 그림 밝기
	public byte			contrast;		// 그림 명암
	public byte			effect;			// 그림 효과 (0:REAL_PIC,1:GRAY_SCALE,2:BLACK_WHTE,4:PATTERN8x8
	public short		binDataID;		// BinItem의 아이디 참조값
	public byte			borderOpaque;	// 테두리 투명도
	public int			instanceID;		// 문서 내 각 개체에 대한 고유 아이디(instance ID)
	public int			picEffectInfo;	// 그림효과정보(그림자,네온,부드러운,가장자리,반사)
	public List<PicEffect>	picEffect;		// 각 효과 정보
	
	public int			iniPicWidth;	// 그림 최초 생성시 기준 이미지 크기
	public int			iniPicHeight;	// 그림 최초 생성시 기준 이미지 크기
	public byte			picAlpha;		// 이미지 투명도
	
	public Ctrl_ShapePic(String ctrlId, int size, byte[] buf, int off, int version) {
		super(ctrlId, size, buf, off, version);
		this.size = offset-off;

		log.fine("                                                  " + toString());
	}

	public Ctrl_ShapePic(Ctrl_GeneralShape shape) {
		super(shape);
		
		this.size = shape.getSize();
	}

	public Ctrl_ShapePic(String ctrlId, Node node, int version) throws NotImplementedException {
        super(ctrlId, node, version);
        
        NamedNodeMap attributes = node.getAttributes();
        // attributes.getNamedItem("reverse").getNodeValue();
        String numStr;
        
        NodeList nodeList = node.getChildNodes();
        for (int i=0; i<nodeList.getLength(); i++) {
            Node child = nodeList.item(i);
            switch(child.getNodeName()) {
            case "hp:lineShape":    // 테두리선 모양
                {
                    NamedNodeMap childAttrs = child.getAttributes();
                    numStr = childAttrs.getNamedItem("color").getNodeValue().replaceAll("#", "");   // 선색상
                    borderColor = Integer.parseInt(numStr);
                    numStr = childAttrs.getNamedItem("width").getNodeValue();                       // 선 굵기
                    borderThick = Integer.parseInt(numStr);
                    // childAttrs.getNamedItem("style").getNodeValue();                       // 선 종류
                    // childAttrs.getNamedItem("endCap").getNodeValue();                      // 선 끝 모양
                    // childAttrs.getNamedItem("headStyle").getNodeValue();                   // 화살표 시작 모양
                    // childAttrs.getNamedItem("tailStyle").getNodeValue();                   // 화살표 끝 모양
                    // childAttrs.getNamedItem("headSz").getNodeValue();                      // 화살표 시작 크기
                    // childAttrs.getNamedItem("tailSz").getNodeValue();                      // 화살표 끝 크기
                    // childAttrs.getNamedItem("outlineStyle").getNodeValue();                // 테두리선의 형태
                    // childAttrs.getNamedItem("alpha").getNodeValue();                       // 투명도       
                }
                break;
            case "hp:imgRect":      // 이미지 좌표 정보
                {
                    NodeList childNodeList = child.getChildNodes();
                    for (int j=0; j<childNodeList.getLength(); j++) {
                        Node grandChild = childNodeList.item(j);
                        NamedNodeMap childAttrs = grandChild.getAttributes();
                        switch(grandChild.getNodeName()) {
                        case "hp:pt0":  // 첫번째 좌표
                            borderPoints[0] = new Point();
                            numStr = childAttrs.getNamedItem("x").getNodeValue();
                            borderPoints[0].x = Integer.parseInt(numStr);
                            numStr = childAttrs.getNamedItem("y").getNodeValue();
                            borderPoints[0].y = Integer.parseInt(numStr);
                            break;
                        case "hp:pt1":  // 두번째 좌표
                            borderPoints[1] = new Point();
                            numStr = childAttrs.getNamedItem("x").getNodeValue();
                            borderPoints[1].x = Integer.parseInt(numStr);
                            numStr = childAttrs.getNamedItem("y").getNodeValue();
                            borderPoints[1].y = Integer.parseInt(numStr);
                            break;
                        case "hp:pt2":  // 세번째 좌표
                            borderPoints[2] = new Point();
                            numStr = childAttrs.getNamedItem("x").getNodeValue();
                            borderPoints[2].x = Integer.parseInt(numStr);
                            numStr = childAttrs.getNamedItem("y").getNodeValue();
                            borderPoints[2].y = Integer.parseInt(numStr);
                            break;
                        case "hp:pt3":  // 네번째 좌표
                            borderPoints[3] = new Point();
                            numStr = childAttrs.getNamedItem("x").getNodeValue();
                            borderPoints[3].x = Integer.parseInt(numStr);
                            numStr = childAttrs.getNamedItem("y").getNodeValue();
                            borderPoints[3].y = Integer.parseInt(numStr);
                            break;
                        }
                    }
                }
                break;
            case "hp:imgClip":      // 이미지 자르기 정보
                {
                    NamedNodeMap childAttrs = child.getAttributes();
                    numStr = childAttrs.getNamedItem("left").getNodeValue();   // 왼쪽에서 이미지를 자른 크기
                    cropLeft = Integer.parseInt(numStr);
                    numStr = childAttrs.getNamedItem("right").getNodeValue();  // 오른쪽에서 이미지를 자른 크기
                    cropRight = Integer.parseInt(numStr);
                    numStr =  childAttrs.getNamedItem("top").getNodeValue();   // 위쪽에서 이미지를 자른 크기
                    cropTop = Integer.parseInt(numStr);
                    numStr =  childAttrs.getNamedItem("bottom").getNodeValue();// 아래쪽에서 이미지를 자른 크기
                    cropBottom = Integer.parseInt(numStr);
                }
                break;
            case "hp:effects":      // 이미지 효과 정보
                {
                    NodeList childNodeList = child.getChildNodes();
                    for (int j=0; j<childNodeList.getLength(); j++) {
                        Node grandChild = childNodeList.item(j);
                        NamedNodeMap childAttrs = grandChild.getAttributes();
                        switch(grandChild.getNodeName()) {
                        case "hp:shadow":  // 그림자 효과
                            {
                                PicEffectType effectType = PicEffectType.SHADOW;
                                Shadow effect = new Shadow(effectType, grandChild, version);
                                picEffect.add(effect);
                            }
                            break;
                        case "hp:glow":  // 네온 효과
                            {
                                PicEffectType effectType = PicEffectType.NEON;
                                Neon effect = new Neon(effectType, grandChild, version);
                                picEffect.add(effect);
                            }
                            break;
                        case "hp:softEdge":  // 부드러운 가장자리 효과
                            {
                                PicEffectType effectType = PicEffectType.SOFT_EDGE;
                                SoftEdge effect = new SoftEdge(effectType, grandChild, version);
                                picEffect.add(effect);
                            }
                            break;
                        case "hp:reflection":  // 반사 효과
                            {
                                PicEffectType effectType = PicEffectType.REFLECT;
                                Reflect effect = new Reflect(effectType, grandChild, version);
                                picEffect.add(effect);
                            }
                            break;
                        }
                    }
                }
                break;
            case "hp:inMargin":     // 안쪽 여백 정보
                {
                    /*
                    NamedNodeMap childAttrs = child.getAttributes();
                    numStr = childAttrs.getNamedItem("left").getNodeValue();
                    numStr = childAttrs.getNamedItem("right").getNodeValue();
                    numStr = childAttrs.getNamedItem("top").getNodeValue();
                    numStr = childAttrs.getNamedItem("bottom").getNodeValue();
                    */
                }
                break;
            case "hp:img":          // 그림 정보    51 page
                {
                    NamedNodeMap childAttrs = child.getAttributes();
                    numStr = childAttrs.getNamedItem("bright").getNodeValue();   // 그림의 밝기
                    bright = (byte) Integer.parseInt(numStr);
                    numStr = childAttrs.getNamedItem("contrast").getNodeValue();  // 그림의 명암
                    contrast = (byte) Integer.parseInt(numStr);
                    switch(childAttrs.getNamedItem("effect").getNodeValue()) {   // 그림의 추가 효과
                    case "REAL_PIC":
                        effect = 0;    break;
                    case "GRAY_SCALE":
                        effect = 1;    break;
                    case "BLACK_WHITE":
                        effect = 2;    break;
                    }
                    numStr =  childAttrs.getNamedItem("binaryItemIDRef").getNodeValue();// BinDataItem 요소의 아이디 참조값
                    binDataID = (short) Integer.parseInt(numStr);
                }
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
