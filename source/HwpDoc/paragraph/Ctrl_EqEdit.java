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
import java.util.logging.Logger;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import HwpDoc.Exception.HwpParseException;
import HwpDoc.Exception.NotImplementedException;

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
	
	public Ctrl_EqEdit(String ctrlId) {
        super(ctrlId);
    }

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
            default:
                throw new NotImplementedException("EqEdit");
            }
        }
        this.fullfilled = true;
    }
	
    public static int parseElement(Ctrl_EqEdit obj, int size, byte[] buf, int off, int version) throws HwpParseException, NotImplementedException {
        int offset = off;
        
        obj.attr        = buf[offset+3]<<24&0xFF000000 | buf[offset+2]<<16&0x00FF0000 | buf[offset+1]<<8&0x0000FF00 | buf[offset]&0x000000FF;
        offset += 4;
        short len       = (short) ((buf[offset+1]<<8&0xFF00 | buf[offset]&0x00FF)*2);
        offset += 2;
        obj.eqn         = new String(buf, offset, len, StandardCharsets.UTF_16LE);
        offset += len;
        obj.charSize    = buf[offset+3]<<24&0xFF000000 | buf[offset+2]<<16&0x00FF0000 | buf[offset+1]<<8&0x0000FF00 | buf[offset]&0x000000FF;
        offset += 4;
        obj.color       = buf[offset+3]<<24&0xFF000000 | buf[offset]<<16&0x00FF0000 | buf[offset+1]<<8&0x0000FF00 | buf[offset+2]&0x000000FF;
        offset += 4;
        obj.baseline    = buf[offset+3]<<24&0xFF000000 | buf[offset+2]<<16&0x00FF0000 | buf[offset+1]<<8&0x0000FF00 | buf[offset]&0x000000FF;
        offset += 4;
        if (size-(offset-off) > 0) {
            len             = (short) ((buf[offset+1]<<8&0xFF00 | buf[offset]&0x00FF)*2);
            offset += 2;
        }
        if (size-(offset-off) > 0) {
            obj.version     = new String(buf, offset, len, StandardCharsets.UTF_16LE);
            offset += len;
        }
        if (offset-off+2 <= size) { // 5.0.33 버전에서는 이 부분 없음
            len             = (short) ((buf[offset+1]<<8&0xFF00 | buf[offset]&0x00FF)*2);
            offset += 2;
            if (offset-off+len <= size) {
                obj.font        = new String(buf, offset, len, StandardCharsets.UTF_16LE);
                offset += len;
            }
        }
        
        if (offset-off-size!=0) {
            log.fine("[CtrlId]=" + obj.ctrlId + ", size=" + size + ", but currentSize=" + (offset-off));
            throw new HwpParseException();
        }
        obj.fullfilled = true;

        return offset-off;
    }
    
    public static int parseCtrl(Ctrl_EqEdit shape, int size, byte[] buf, int off, int version) throws HwpParseException, NotImplementedException {
        int offset = off;
        
        int len = Ctrl_Common.parseCtrl(shape, size, buf, offset, version);
        offset += len;

        return offset-off;
    }

    public static int parseListHeaderAppend(Ctrl_GeneralShape obj, int size, byte[] buf, int off, int version) throws HwpParseException, NotImplementedException {
        int offset = off;
        if (size==24) {
            offset += 2;
            obj.captionAttr     = buf[offset+3]<<24&0xFF000000 | buf[offset+2]<<16&0x00FF0000 | buf[offset+1]<<8&0x0000FF00 | buf[offset]&0x000000FF;
            offset += 4;
            obj.captionWidth    = buf[offset+3]<<24&0xFF000000 | buf[offset+2]<<16&0x00FF0000 | buf[offset+1]<<8&0x0000FF00 | buf[offset]&0x000000FF;
            offset += 4;
            obj.captionSpacing  = (short) (buf[offset+1]<<8&0xFF00 | buf[offset]&0x00FF);
            offset += 2;
            obj.captionMaxW     = buf[offset+3]<<24&0xFF000000 | buf[offset+2]<<16&0x00FF0000 | buf[offset+1]<<8&0x0000FF00 | buf[offset]&0x000000FF;
            offset += 4;
            offset += 8;
        }
        
        log.fine("                                                  ctrlID="+obj.ctrlId+", 캡션 parsing이지만, 정확한 parsing은 어떻게 해야 하는지 알 수 없음.");
        
        if (offset-off-size!=0) {
            log.fine("[CtrlID]=" + obj.ctrlId + ", size=" + size + ", but currentSize=" + (offset-off));
            throw new HwpParseException();
        }
        
        return offset-off;
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
