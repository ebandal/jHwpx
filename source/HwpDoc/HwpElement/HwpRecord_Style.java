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
package HwpDoc.HwpElement;

import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import HwpDoc.HwpDocInfo;
import HwpDoc.Exception.HwpParseException;

public class HwpRecord_Style extends HwpRecord {
	private static final Logger log = Logger.getLogger(HwpRecord_Style.class.getName());
	private HwpDocInfo	parent;

	public String		name;				// 로컬 스타일 이름. 한글 윈도우에서는 한글 스타일 이름
	public String		engName;			// 영문 스타일 이름.
	public byte			type;				// 속성
	public byte			nextStyle;			// 다음 스타일 아이디 참조값
	public short		langId;				// 언어 아이디
	public short		paraShape;			// 문단 모양 아이디 참조값(문단 모양의 아이디 속성)
	public short		charShape;			// 글자 모양 아이디 참조값(글자 모양의 아이디 속성)
	public boolean		lockForm;			// 양식모드에서 style 보호하기
	
	HwpRecord_Style(int tagNum, int level, int size) {
		super(tagNum, level, size);
	}
	
	public HwpRecord_Style(HwpDocInfo docInfo, int tagNum, int level, int size, byte[] buf, int off, int version) throws HwpParseException {
		this(tagNum, level, size);
		this.parent = docInfo;

		int offset = off;
		int styleNameLen1 = (buf[offset+1]<<8&0xFF00 | buf[offset]&0x00FF)*2;
		offset += 2;
		if (styleNameLen1 > 0) {
			name 		= new String(buf, offset, styleNameLen1, StandardCharsets.UTF_16LE);
			offset += styleNameLen1;
		}
		int styleNameLen2 = (buf[offset+1]<<8&0xFF00 | buf[offset]&0x00FF)*2;
		offset += 2;
		if (styleNameLen2 > 0) {
			engName 	= new String(buf, offset, styleNameLen2, StandardCharsets.UTF_16LE);
			offset += styleNameLen2;
		}
		type 			= (byte) (buf[offset++]&0x00FF);
		nextStyle		= (byte) (buf[offset++]&0x00FF);
		
		langId 			= (short) (buf[offset+1]<<8&0xFF00 | buf[offset]&0x00FF);
		offset += 2;
		paraShape	 	= (short) (buf[offset+1]<<8&0xFF00 | buf[offset]&0x00FF);
		offset += 2;
		charShape	 	= (short) (buf[offset+1]<<8&0xFF00 | buf[offset]&0x00FF);
		offset += 2;
		
		log.fine("                                                  "
				+"ID="+(parent.styleList.size())
				+",스타일="+name
				+",스타일구분="+(type==0?"문단스타일":type==1?"글자스타일":"알수없음")
				+",문단모양ID="+paraShape
				+",글자모양ID="+charShape
				);
		
		if (offset-off-size!=0 && offset-off!=12+styleNameLen1+styleNameLen2) {
			log.fine("[TAG]=" + tag.toString() + ", size=" + size + ", but currentSize=" + (offset-off));
			dump(buf, off, size);
			throw new HwpParseException();
		}
	}

    public HwpRecord_Style(HwpDocInfo docInfo, Node node, int version) {
        super(HwpTag.HWPTAG_STYLE, 0, 0);
        this.parent = docInfo;

        dumpNode(node, 1);
        
        NamedNodeMap attributes = node.getAttributes();
        
        // id값은 처리하지 않는다. List<HwpRecord_Style>에 순차적으로 추가한다.
        // String id = attributes.getNamedItem("id").getNodeValue();
        
        switch(attributes.getNamedItem("type").getNodeValue()) {
        case "PARA":
            type = 0;   break;
        case "CHAR":
            type = 1;   break;
        }

        name = attributes.getNamedItem("name").getNodeValue();
        engName = attributes.getNamedItem("engName").getNodeValue();
        
        String numStr = attributes.getNamedItem("paraPrIDRef").getNodeValue();
        paraShape = (short) Integer.parseInt(numStr);
        
        numStr = attributes.getNamedItem("charPrIDRef").getNodeValue();
        charShape = (short) Integer.parseInt(numStr);
        
        numStr = attributes.getNamedItem("nextStyleIDRef").getNodeValue();
        nextStyle = (byte) Integer.parseInt(numStr);
        
        numStr = attributes.getNamedItem("langID").getNodeValue();
        langId = (short) Integer.parseInt(numStr);
        
        switch(attributes.getNamedItem("lockForm").getNodeValue()) {
        case "0":
            lockForm = false;   break;
        case "1":
            lockForm = true;   break;
        }
    }
}
