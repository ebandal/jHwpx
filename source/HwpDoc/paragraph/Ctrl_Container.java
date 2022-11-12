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

import HwpDoc.Exception.HwpParseException;
import HwpDoc.Exception.NotImplementedException;

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

    public Ctrl_Container(String ctrlId, Node node, int version) throws NotImplementedException {
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
            default:
                throw new NotImplementedException("Ctrl_Container");
            }
        }
    }
    
    public static int parseElement(Ctrl_Container obj, int size, byte[] buf, int off, int version) throws HwpParseException, NotImplementedException {
        int offset = off;
        
        obj.nElement        = (short) (buf[offset+1]<<8&0xFF00 | buf[offset]&0x00FF);
        offset += 2;
        if (obj.nElement>0) {
            if (obj.ctrlIdList==null) 
                obj.ctrlIdList = new ArrayList<String>();
            for (int i=0;i<obj.nElement;i++) {
                String ctrlId = new String(buf, offset, 4, StandardCharsets.US_ASCII);
                obj.ctrlIdList.add(ctrlId);
                offset += 4;
            }
        }
        
        obj.list = new ArrayList<Ctrl_GeneralShape>();
        for (int i=0;i<obj.nElement;i++) {
            
            String ctrlId = new String(buf, offset, 4, StandardCharsets.US_ASCII);
            offset += 4;
            Ctrl_GeneralShape chldObj = null;
            
            // ctrlId를 거꾸로 읽어 비교한다.
            switch(ctrlId) {
            case "cip$":    // 그림
                chldObj = new Ctrl_ShapePic(ctrlId, size-(offset-off), buf, offset, version);
                offset += chldObj.getSize();
                offset += Ctrl_ShapePic.parseCtrl((Ctrl_ShapePic)chldObj, size, buf, offset, version);
                chldObj.ctrlId = ctrlId;
                break;
            case "cer$":    // 사각형
                chldObj = new Ctrl_ShapeRect(ctrlId, size-(offset-off), buf, offset, version);
                offset += chldObj.getSize();
                offset += Ctrl_ShapeRect.parseCtrl((Ctrl_ShapeRect)chldObj, size, buf, offset, version);
                chldObj.ctrlId = ctrlId;
                break;
            case "nil$":    // 선
                chldObj = new Ctrl_ShapeLine(ctrlId, size-(offset-off), buf, offset, version);
                offset += chldObj.getSize();
                offset += Ctrl_ShapeLine.parseCtrl((Ctrl_ShapeLine)chldObj, size, buf, offset, version);
                chldObj.ctrlId = ctrlId;
                break;
            case "noc$":    // 묶음 개체
                chldObj = new Ctrl_Container(ctrlId, size-(offset-off), buf, offset, version);
                offset += chldObj.getSize();
                offset += Ctrl_Container.parseCtrl((Ctrl_Container)chldObj, size, buf, offset, version);
                chldObj.ctrlId = ctrlId;
                break;
            case "lle$":    // 타원
                chldObj = new Ctrl_ShapeEllipse(ctrlId, size-(offset-off), buf, offset, version);
                offset += chldObj.getSize();
                offset += Ctrl_ShapeEllipse.parseCtrl((Ctrl_ShapeEllipse)chldObj, size, buf, offset, version);
                chldObj.ctrlId = ctrlId;
                break;
            case "lop$":    // 다각형
                chldObj = new Ctrl_ShapePolygon(ctrlId, size-(offset-off), buf, offset, version);
                offset += chldObj.getSize();
                offset += Ctrl_ShapePolygon.parseCtrl((Ctrl_ShapePolygon)chldObj,  size, buf, offset, version);
                chldObj.ctrlId = ctrlId;
                break;
            case "cra$":    // 호
                chldObj = new Ctrl_ShapeArc(ctrlId, size-(offset-off), buf, offset, version);
                offset += chldObj.getSize();
                offset += Ctrl_ShapeArc.parseCtrl((Ctrl_ShapeArc)chldObj, size, buf, offset, version);
                chldObj.ctrlId = ctrlId;
                break;
            case "ruc$":    // 곡선
                chldObj = new Ctrl_ShapeCurve(ctrlId, size-(offset-off), buf, offset, version);
                offset += chldObj.getSize();
                offset += Ctrl_ShapeCurve.parseCtrl((Ctrl_ShapeCurve)chldObj, size, buf, offset, version);
                chldObj.ctrlId = ctrlId;
                break;
            case "elo$":    // OLE
                chldObj = new Ctrl_ShapeOle(ctrlId, size-(offset-off), buf, offset, version);
                offset += chldObj.getSize();
                offset += Ctrl_ShapeOle.parseCtrl((Ctrl_ShapeOle)chldObj, size, buf, offset, version);
                chldObj.ctrlId = ctrlId;
                break;
            default:
                log.severe("Neither known ctrlID=" + ctrlId+" nor implemented.");
                throw new NotImplementedException(ctrlId);
            }
            obj.list.add(chldObj);
        }
        log.finest("[묶음 개체]를 읽었습니다.");
        
        return offset-off;
    }
    
    public static int parseCtrl(Ctrl_Container obj, int size, byte[] buf, int off, int version) throws HwpParseException, NotImplementedException {
        int offset = off;
        
        // 문서에는 나와있지 않으나, 개체요소속성 (표 83)이 포함된다.
        offset += Ctrl_ObjElement.parseCtrl((Ctrl_ObjElement)obj, size, buf, offset, version);;
        
        obj.nElement        = (short) (buf[offset+1]<<8&0xFF00 | buf[offset]&0x00FF);
        offset += 2;
        if (obj.nElement>0) {
            if (obj.ctrlIdList==null) 
                obj.ctrlIdList = new ArrayList<String>();
            for (int i=0;i<obj.nElement;i++) {
                String ctrlId = new String(buf, offset, 4, StandardCharsets.US_ASCII);
                obj.ctrlIdList.add(ctrlId);
                offset += 4;
            }
        }
        
        // 끝에 정보를 알수 없는 4 byte가 붙는다.  문서에도 나와있지 않다.
        offset += 4;
        
        return offset-off;
    }

}
