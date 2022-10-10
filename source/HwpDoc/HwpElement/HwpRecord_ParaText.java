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

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import HwpDoc.Exception.HwpParseException;

public class HwpRecord_ParaText extends HwpRecord {
	private static final Logger log = Logger.getLogger(HwpRecord_ParaText.class.getName());

	HwpRecord_ParaText(int tagNum, int level, int size) {
		super(tagNum, level, size);
	}
	
	public static String parse(int tagNum, int level, int size, byte[] buf, int off, int version) throws HwpParseException {
		int offset = off;
		
		String text = new String(buf, offset, size, StandardCharsets.UTF_16LE);
		offset += size;

		String readable = toReadableString(text);
		log.fine("                                                  "+readable);

		if (offset-off-size != 0) {
			log.fine("[TAG]=" + tagNum + ", size=" + size + ", but currentSize=" + (offset-off));
			dump(buf, off, size);
			throw new HwpParseException();
		}
		return text;
	}
	
	private static String toReadableString(String text) {
		StringBuffer sb = new StringBuffer();
		char extendChar = 0;
		char inlineChar = 0;
		ByteBuffer bb = ByteBuffer.allocate(12).order(ByteOrder.LITTLE_ENDIAN);
		
		for(int i=0;i<text.length();i++) {
			char c = text.charAt(i);
			if (extendChar==0 && inlineChar==0 && c>31) {
				sb.append(c);
			} else if (extendChar!=0) {
				if (c==extendChar) {
					sb.append(new String(bb.array(), StandardCharsets.US_ASCII).trim()+"[/EXT("+(int)c+")]");
					((Buffer)bb).clear();	// Java9 이후와 호환을 위해
					extendChar=0;
				} else {
					bb.putChar(c);
				}
			} else if (inlineChar!=0) {
				if (c==inlineChar) {
					switch(c) {
					case 9:
						sb.append(HwpRecord.toHexString(bb.array())+"[/INL("+(int)c+")]"); 
						break;
					default:
						sb.append(new String(bb.array(), StandardCharsets.US_ASCII).trim()+"[/INL("+(int)c+")]");
					}
					((Buffer)bb).clear();	// Java9 이후와 호환을 위해
					inlineChar=0;
				} else {
					bb.putChar(c);
				}
			} else if (c <= 31) {
				switch(c) {
				case 13:
					sb.append("[END]");
					break;
				case 0:
				case 10:
				case 24:
				case 25:
				case 26:
				case 27:
				case 28:
				case 29:
				case 30:
				case 31:
					sb.append("[CHAR("+(int)c+")]");
					break;
				case 1:
				case 2:
				case 3:
				case 11:
				case 12:
				case 14:
				case 15:
				case 16:
				case 17:
				case 18:
				case 21:
				case 22:
				case 23:
					sb.append("[EXT("+(int)c+")]");
					((Buffer)bb).clear();	// Java9 이후와 호환을 위해
					extendChar = c;
					break;
				case 4:
				case 5:
				case 6:
				case 7:
				case 8:
				case 9:
				case 19:
				case 20:
					sb.append("[INL("+(int)c+")]");
					((Buffer)bb).clear();	// Java9 이후와 호환을 위해
					inlineChar = c;
					break;
				default:
					bb.putChar(c);
				}
			}
		}
		return sb.toString();
	}

}
