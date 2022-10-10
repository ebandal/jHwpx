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

import HwpDoc.HwpElement.HwpRecordTypes.NumberShape2;
import HwpDoc.paragraph.Ctrl_AutoNumber.NumType;


public class Ctrl_PageNumPos extends Ctrl {
	private static final Logger log = Logger.getLogger(Ctrl_PageNumPos.class.getName());
	private int size;
	
	public NumPos 		pos;
	public NumberShape2	numShape;
	public String  		userDef;
	public String		prefix;
	public String		postfix;
	public String		constantDash;
	
	public Ctrl_PageNumPos(String ctrlId, int size, byte[] buf, int off, int version) {
		super(ctrlId);
		int offset 		= off;

		int attr 		= buf[offset+3]<<24&0xFF000000 | buf[offset+2]<<16&0x00FF0000 | buf[offset+1]<<8&0x0000FF00 | buf[offset]&0x000000FF;
		numShape		= NumberShape2.from(attr&0xFF);
		pos 			= NumPos.from((attr>>8)&0xF);
		
		offset += 2;
		userDef 		= new String(buf, offset, 2, StandardCharsets.UTF_16LE);
		offset += 2;
		prefix 			= new String(buf, offset, 2, StandardCharsets.UTF_16LE);
		offset += 2;
		postfix 		= new String(buf, offset, 2, StandardCharsets.UTF_16LE);
		offset += 2;
		constantDash 	= new String(buf, offset, 2, StandardCharsets.UTF_16LE);
		offset += 2;
		
		log.fine("                                                  " + toString());
		this.size = offset-off;
	}
	
	public Ctrl_PageNumPos(String ctrlId, Node node, int version) {
	    super(ctrlId);
	    
        NamedNodeMap attributes = node.getAttributes();
        
        pos = NumPos.valueOf(attributes.getNamedItem("pos").getNodeValue());
        numShape = NumberShape2.valueOf(attributes.getNamedItem("formatType").getNodeValue());
        // attributes.getNamedItem("sideChar").getNodeValue();
    }

    public String toString() {
		StringBuffer strb = new StringBuffer();
		strb.append("CTRL("+ctrlId+")")
			.append("=공통속성:"+super.toString());
		return strb.toString();
	}

	@Override
	public int getSize() {
		return this.size;
	}
	
	public static enum NumPos {
		NONE			(0x0),	// 쪽 번호 없음
		LEFT_TOP		(0x1),	// 왼쪽 위
		CENTER_TOP		(0x2),	// 가운데 위
		RIGHT_TOP		(0x3),	// 오른쪽 위
		LEFT_BOTTOM		(0x4),	// 왼쪽 아래
		CENTER_BOTTOM	(0x5),	// 가운데 아래
		RIGHT_BOTTOM	(0x6),	// 오른쪽 아래
		OUTER_TOP		(0x7),	// 바깥쪽 위
		OUTER_BOTTOM	(0x8),	// 바깥쪽 아래
		INNER_TOP		(0x9),	// 안쪽 위
		INNER_BOTTOM	(0x10);	// 안쪽 아래
		
		private int num;
	    private NumPos(int num) { 
	    	this.num = num;
	    }
	    public static NumPos from(int num) {
	    	for (NumPos type: values()) {
	    		if (type.num == num)
	    			return type;
	    	}
	    	return null;
	    }
	}

}
