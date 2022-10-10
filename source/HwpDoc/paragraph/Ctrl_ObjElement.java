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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import HwpDoc.Exception.NotImplementedException;
import HwpDoc.paragraph.Ctrl_Table.CellZone;

public class Ctrl_ObjElement extends Ctrl_Common {
	private static final Logger log = Logger.getLogger(Ctrl_ObjElement.class.getName());
	private int size;

	public int		xGrpOffset;	// 개체가 속한 그룹 내에서의 X offset
	public int		yGrpOffset;	// 개체가 속한 그룹 내에서의 Y offset
	public short	nGrp;		// 몇번이나 그룹 되었는지
	public short	ver;		// 개체 요소의 local file version
	public int 		iniWidth;	// 개체 생성시 초기 폭
	public int		iniHeight;	// 개체 생성시 초기 높이
	public int		curWidth;	// 개체의 현재 폭
	public int		curHeight;	// 개체의 현재 높이
	public boolean  horzFlip;	// 속성(0:horz flip, 1:ver flip)
    public boolean  verFlip;    // 속성(0:horz flip, 1:ver flip)
	public short	rotat;		// 회전각
	public int		xCenter;	// 회전 중심의 x 자표
	public int		yCenter;	// 회전 중심의 y 자표
	public short	matCnt;		// scale matrix와 ratation matrix쌍의 갯수. 초기엔 1, group할때마다 하나씩 증가하고, ungroup할때마다 하나씩 감소한다.
	public double[]	matrix;		// transalation matrix
	public double[]	matrixSeq;	// scale matrix/rotation matrix sequence

	public Ctrl_ObjElement() {
		super();
	}

	public Ctrl_ObjElement(String ctrlId, int size, byte[] buf, int off, int version) {
		super(ctrlId, size, buf, off, version);
		this.size = offset-off;

		log.fine("                                                  " + toString());
	}
	
	public Ctrl_ObjElement(Ctrl_ObjElement element) {
		super((Ctrl_Common)element);
		
		this.xGrpOffset = element.xGrpOffset;
		this.yGrpOffset = element.yGrpOffset;
		this.nGrp 		= element.nGrp;
		this.ver 		= element.ver;
		this.iniWidth 	= element.iniWidth;
		this.iniHeight 	= element.iniHeight;
		this.curWidth 	= element.curWidth;
		this.curHeight 	= element.curHeight;
		this.horzFlip 	= element.horzFlip;
        this.verFlip    = element.verFlip;
		this.rotat 		= element.rotat;
		this.xCenter 	= element.xCenter;
		this.yCenter 	= element.yCenter;
		this.matCnt 	= element.matCnt;
		this.matrix 	= element.matrix;
		this.matrixSeq 	= element.matrixSeq;
	}
	
	public Ctrl_ObjElement(String ctrlId, Node node, int version) throws NotImplementedException {
	    super(ctrlId, node, version);
	    
        NamedNodeMap attributes = node.getAttributes();
        
        switch(attributes.getNamedItem("href").getNodeValue()) {
        case "0":
            break;
        default:
            throw new NotImplementedException("Ctrl_ObjElement");
        }
        
        String numStr = attributes.getNamedItem("groupLevel").getNodeValue();
        nGrp = (short) Integer.parseInt(numStr);

        // numStr = attributes.getNamedItem("InstId").getNodeValue();

        int matrixIdx = 0;
        NodeList nodeList = node.getChildNodes();
        for (int i=0; i<nodeList.getLength(); i++) {
            Node child = nodeList.item(i);
            
            switch(child.getNodeName()) {
            case "hp:offset":
                {
                    NamedNodeMap childAttrs = child.getAttributes();
                    numStr = childAttrs.getNamedItem("x").getNodeValue();
                    xGrpOffset = Integer.parseInt(numStr);
                    numStr = childAttrs.getNamedItem("y").getNodeValue();
                    yGrpOffset = Integer.parseInt(numStr);
                }
                break;
            case "hp:orgSz":
                {
                    NamedNodeMap childAttrs = child.getAttributes();
                    numStr = childAttrs.getNamedItem("width").getNodeValue();
                    iniWidth = Integer.parseInt(numStr);
                    numStr = childAttrs.getNamedItem("height").getNodeValue();
                    iniHeight = Integer.parseInt(numStr);
                }
                break;
            case "hp:curSz":
                {
                    NamedNodeMap childAttrs = child.getAttributes();
                    numStr = childAttrs.getNamedItem("width").getNodeValue();
                    curWidth = Integer.parseInt(numStr);
                    numStr = childAttrs.getNamedItem("height").getNodeValue();
                    curHeight = Integer.parseInt(numStr);
                }
                break;
            case "hp:flip":
                {
                    NamedNodeMap childAttrs = child.getAttributes();
                    switch(childAttrs.getNamedItem("horizontal").getNodeValue()) {
                    case "0":
                        horzFlip = false;   break;
                    case "1":
                        horzFlip = true;    break;
                    }
                    
                    switch(childAttrs.getNamedItem("vertical").getNodeValue()) {
                    case "0":
                        verFlip = false;   break;
                    case "1":
                        verFlip = true;    break;
                    }
                }
                break;
            case "hp:rotationInfo":
                {
                    NamedNodeMap childAttrs = child.getAttributes();
                    numStr = childAttrs.getNamedItem("angle").getNodeValue();
                    rotat = (short) Integer.parseInt(numStr);

                    numStr = childAttrs.getNamedItem("centerX").getNodeValue();
                    xCenter = Integer.parseInt(numStr);

                    numStr = childAttrs.getNamedItem("centerY").getNodeValue();
                    yCenter = Integer.parseInt(numStr);
                    // childAttrs.getNamedItem("rotateimage").getNodeValue()) {
                }
                break;
            case "hp:renderingInfo":
                {
                    matrix = new double[nGrp*6];
                    matrixSeq = new double[nGrp*6*2];
                    
                    NodeList childNodeList = child.getChildNodes();
                    for (int j=0; j<childNodeList.getLength(); j++) {
                        Node grandChild = childNodeList.item(j);
                        
                        switch(grandChild.getNodeName()) {
                        case "hp:transMatrix":
                            setMatrix(grandChild, matrix, matrixIdx*6);
                            break;
                        case "hp:scaMatrix":
                            setMatrix(grandChild, matrixSeq, matrixIdx*6*2);
                            break;
                        case "hp:rotMatrix":
                            setMatrix(grandChild, matrixSeq, matrixIdx*6*2+6);
                            break;
                        }
                    }
                    matrixIdx++;
                }
                break;
            }
        }
    }

	private void setMatrix(Node node, double[] matrix, int offset) {
        NamedNodeMap attributes = node.getAttributes();
        String numStr = attributes.getNamedItem("e1").getNodeValue();
        matrix[offset+0] = (short) Float.parseFloat(numStr);
        numStr = attributes.getNamedItem("e2").getNodeValue();
        matrix[offset+1] = (short) Float.parseFloat(numStr);
        numStr = attributes.getNamedItem("e3").getNodeValue();
        matrix[offset+2] = (short) Float.parseFloat(numStr);
        numStr = attributes.getNamedItem("e4").getNodeValue();
        matrix[offset+3] = (short) Float.parseFloat(numStr);
        numStr = attributes.getNamedItem("e5").getNodeValue();
        matrix[offset+4] = (short) Float.parseFloat(numStr);
        numStr = attributes.getNamedItem("e6").getNodeValue();
        matrix[offset+5] = (short) Float.parseFloat(numStr);
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
