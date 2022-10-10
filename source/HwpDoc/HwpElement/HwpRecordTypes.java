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

public class HwpRecordTypes {

	public static enum LineType1 {
		SOLID				(0),	// 실선
		DASH				(1),	// 긴 점선
		DOT					(2),	// 점선
		DASH_DOT			(3),	// -.-.-.-
		DASH_DOT_DOT		(4),	// -..-..-..-
		LONG_DASH			(5),	// Dash 보다 긴 선분의 반복
		CIRCLE				(6),	// Dot보다 큰 동그라미의 반복
		DOUBLE_SLIM			(7),	// 2중선
		SLIM_THICK			(8),	// 가는선+굵은선 2중선
		THICK_SLIM			(9),	// 굵은선+가는선 2중선
		SLIM_THICK_SLIM		(10), 	// 가는선+굵은선+가는선 3중선
		WAVE				(11),	// 물결
		DOUBLE_WAVE			(12),	// 물결 2중선
		THICK_3D			(13),	// 두꺼운 3D
		THICK_3D_REVERS_LI	(14),	// 두꺼운 3D(광원 반대)
		SOLID_3D			(15),	// 3D 단선
		SOLID_3D_REVERS_LI	(16);	// 3D 단선(광원 반대)
		
		private int num;
	    private LineType1(int num) { 
	    	this.num = num;
	    }
	    public static LineType1 from(int num) {
	    	for (LineType1 shape: values()) {
	    		if (shape.num == num)
	    			return shape;
	    	}
	    	return SOLID;
	    }
	}
	
	public static enum LineType2 {
		NONE				(0),
		SOLID				(1),	// 실선
		DASH				(2),	// 긴 점선
		DOT					(3),	// 점선
		DASH_DOT			(4),	// -.-.-.-
		DASH_DOT_DOT		(5),	// -..-..-..-
		LONG_DASH			(6),	// Dash 보다 긴 선분의 반복
		CIRCLE				(7),	// Dot보다 큰 동그라미의 반복
		DOUBLE_SLIM			(8),	// 2중선
		SLIM_THICK			(9),	// 가는선+굵은선 2중선
		THICK_SLIM			(10),	// 굵은선+가는선 2중선
		SLIM_THICK_SLIM		(11); 	// 가는선+굵은선+가는선 3중선
		
		private int num;
	    private LineType2(int num) { 
	    	this.num = num;
	    }
	    public static LineType2 from(int num) {
	    	for (LineType2 shape: values()) {
	    		if (shape.num == num)
	    			return shape;
	    	}
	    	return NONE;
	    }
	}
	
	public static enum NumberShape1 {
		DIGIT					(0),	// 1, 2, 3
		CIRCLE_DIGIT			(1),	// 동그라미 쳐진 1, 2, 3
		ROMAN_CAPITAL			(2),	// I, II, III
		ROMAN_SMALL				(3),	// i, ii, iii
		LATIN_CAPITAL			(4),	// A, B, C
		LATIN_SMALL				(5),	// a, b, c
		CIRCLED_LATIN_CAPITAL	(6),	// 동그라미 쳐진 A, B, C
		CIRCLED_LATIN_SMALL		(7),	// 동그라미 쳐진 a, b, c
		HANGLE_SYLLABLE			(8),	// 가, 나, 다
		CIRCLED_HANGUL_SYLLABLE	(9),	// 동그라미 쳐진 가, 나, 다
		HANGUL_JAMO				(10), 	// ㄱ, ㄴ, ㄷ
		CIRCLED_HANGUL_JAMO		(11),	// 동그라미 쳐진 ㄱ, ㄴ, ㄷ
		HANGUL_PHONETIC			(12),	// 일, 이 , 삼,
		IDEOGRAPH				(13),	// 一, 二, 三
		CIRCLED_IDEOGRAPH		(14);	// 동그라미 쳐진 一, 二, 三
		
		private int num;
	    private NumberShape1(int num) { 
	    	this.num = num;
	    }
	    public static NumberShape1 from(int num) {
	    	for (NumberShape1 shape: values()) {
	    		if (shape.num == num)
	    			return shape;
	    	}
	    	return DIGIT;
	    }
	}
	public static enum NumberShape2 {
		DIGIT					(0),	// 1, 2, 3
		CIRCLE_DIGIT			(1),	// 동그라미 쳐진 1, 2, 3
		ROMAN_CAPITAL			(2),	// I, II, III
		ROMAN_SMALL				(3),	// i, ii, iii
		LATIN_CAPITAL			(4),	// A, B, C
		LATIN_SMALL				(5),	// a, b, c
		CIRCLED_LATIN_CAPITAL	(6),	// 동그라미 쳐진 A, B, C
		CIRCLED_LATIN_SMALL		(7),	// 동그라미 쳐진 a, b, c
		HANGLE_SYLLABLE			(8),	// 가, 나, 다
		CIRCLED_HANGUL_SYLLABLE	(9),	// 동그라미 쳐진 가, 나, 다
		HANGUL_JAMO				(10), 	// ㄱ, ㄴ, ㄷ
		CIRCLED_HANGUL_JAMO		(11),	// 동그라미 쳐진 ㄱ, ㄴ, ㄷ
		HANGUL_PHONETIC			(12),	// 일, 이 , 삼,
		IDEOGRAPH				(13),	// 一, 二, 三
		CIRCLED_IDEOGRAPH		(14),	// 동그라미 쳐진 一, 二, 三
		DECAGON_CIRCLE			(15),	// 갑, 을, 병, 정, 무, 기, 경, 신, 임, 계
		DECAGON_CRICLE_HANGJA	(16),	// 甲, 乙, 丙, 丁, 戊, 己, 庚, 辛, 壬, 癸
		SYMBOL					(0x80),	// 4가지 문자가 차례로 반복
		USER_CHAR				(0x81);	// 사용자 지정 문자 반복
		
		private int num;
	    private NumberShape2(int num) { 
	    	this.num = num;
	    }
	    public static NumberShape2 from(int num) {
	    	for (NumberShape2 shape: values()) {
	    		if (shape.num == num)
	    			return shape;
	    	}
	    	return DIGIT;
	    }
	}
	
}
