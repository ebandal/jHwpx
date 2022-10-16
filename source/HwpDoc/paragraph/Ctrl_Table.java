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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import HwpDoc.Exception.HwpParseException;
import HwpDoc.Exception.NotImplementedException;
import HwpDoc.HwpElement.HwpRecordTypes.LineType2;

public class Ctrl_Table extends Ctrl_Common {
	private static final Logger log = Logger.getLogger(Ctrl_Table.class.getName());
	private int size;
	
	public int   attr;			       // 표개체 속성의 속성
	public short nRows;                // RowCount
	public short nCols;                // nCols
	public short cellSpacing;          // CellSpacing
	public short inLSpace;             // 안쪽 왼쪽 여백
	public short inRSpace;             // 안쪽 오른쪽 여백
	public short inUSpace;             // 안쪽 위쪽 여백
	public short inDSpace;             // 안쪽 아래쪽 여백
	public short[] rowSize;            // Row size
	public short borderFillID;         // Border Fill ID
	public short validZoneSize;        // Valid Zone Info Size(5.0.1.0 이상)
	public List<CellZone> cellzoneList;	// 영역속성 (표 78 참조) (5.0.1.0 이상)
	public List<TblCell> cells;
		
	public Ctrl_Table(String ctrlId) {
       super(ctrlId);
    }
	
	public Ctrl_Table(String ctrlId, int size, byte[] buf, int off, int version) {
		super(ctrlId, size, buf, off, version);
		this.size = offset-off;

		log.fine("                                                  " + toString());
	}
	
	public Ctrl_Table(String ctrlId, Node node, int version) throws NotImplementedException {
	    super(ctrlId, node, version);
	    
        NamedNodeMap attributes = node.getAttributes();
                
        switch(attributes.getNamedItem("pageBreak").getNodeValue()) {
        case "TABLE":
        case "CELL":
        case "NONE":
            break;
        default:
            throw new NotImplementedException("Ctrl_Table");
        }
        
        switch(attributes.getNamedItem("repeatHeader").getNodeValue()) {
        case "0":
        case "1":
            break;
        default:
            throw new NotImplementedException("Ctrl_Table");
        }

        String numStr = attributes.getNamedItem("rowCnt").getNodeValue();
        nRows = (short) Integer.parseInt(numStr);

        switch(attributes.getNamedItem("noAdjust").getNodeValue()) {
        case "0":
        case "1":
            break;
        default:
            throw new NotImplementedException("Ctrl_Table");
        }

        numStr = attributes.getNamedItem("colCnt").getNodeValue();
        nCols = (short) Integer.parseInt(numStr);

        numStr = attributes.getNamedItem("cellSpacing").getNodeValue();
        cellSpacing = (short) Integer.parseInt(numStr);
        
        numStr = attributes.getNamedItem("borderFillIDRef").getNodeValue();
        borderFillID = (short) Integer.parseInt(numStr);
        
        NodeList nodeList = node.getChildNodes();
        for (int i=0; i<nodeList.getLength(); i++) {
            Node child = nodeList.item(i);
            
            switch(child.getNodeName()) {
            case "hp:inMargin":
                {
                    NamedNodeMap childAttrs = child.getAttributes();
                    numStr = childAttrs.getNamedItem("left").getNodeValue();
                    inLSpace = (short) Integer.parseInt(numStr);
                    numStr = childAttrs.getNamedItem("right").getNodeValue();
                    inRSpace = (short) Integer.parseInt(numStr);
                    numStr = childAttrs.getNamedItem("top").getNodeValue();
                    inUSpace = (short) Integer.parseInt(numStr);
                    numStr = childAttrs.getNamedItem("bottom").getNodeValue();
                    inDSpace = (short) Integer.parseInt(numStr);
                }
                break;
            case "hp:cellzonList":
                {
                    NodeList childNodeList = child.getChildNodes();
                    for (int j=0; j<childNodeList.getLength(); j++) {
                        Node grandChild = childNodeList.item(j);
                        
                        switch(grandChild.getNodeName()) {
                        case "cellzone":
                            CellZone cellzone = new CellZone();
                            NamedNodeMap childAttrs = grandChild.getAttributes();
                            numStr = childAttrs.getNamedItem("startRowAddr").getNodeValue();
                            cellzone.startRowAddr = (short)Integer.parseInt(numStr);
                            numStr = childAttrs.getNamedItem("startColAddr").getNodeValue();
                            cellzone.startColAddr = (short)Integer.parseInt(numStr);
                            numStr = childAttrs.getNamedItem("endRowAddr").getNodeValue();
                            cellzone.endRowAddr = (short)Integer.parseInt(numStr);
                            numStr = childAttrs.getNamedItem("endColAddr").getNodeValue();
                            cellzone.endColAddr = (short)Integer.parseInt(numStr);
                            numStr = childAttrs.getNamedItem("borderFillIDRef").getNodeValue();
                            cellzone.borderFillIDRef = (short)Integer.parseInt(numStr);
                            cellzoneList.add(cellzone);
                            break;
                        }
                    }
                }
                break;
            case "hp:tr":
                {
                    if (cells==null) {
                        cells = new ArrayList<TblCell>();
                    }
                    NodeList childNodeList = child.getChildNodes();
                    for (int j=0; j<childNodeList.getLength(); j++) {
                        Node grandChild = childNodeList.item(j);
                        
                        switch(grandChild.getNodeName()) {
                        case "hp:tc":
                            TblCell cell = new TblCell(grandChild, version);
                            cells.add(cell);
                            break;
                        }
                    }
                }
                break;
            case "hp:label":
                break;
            }
        }
    }

   public static int parseCtrl(Ctrl_Table table, int size, byte[] buf, int off, int version) throws HwpParseException {
        int offset = off;
        
        table.attr          = buf[offset+3]<<24&0xFF000000 | buf[offset+2]<<16&0x00FF0000 | buf[offset+1]<<8&0x0000FF00 | buf[offset]&0x000000FF;
        offset += 4;
        table.nRows         = (short) (buf[offset+1]<<8&0xFF00 | buf[offset]&0x00FF);
        offset += 2;
        table.nCols         = (short) (buf[offset+1]<<8&0xFF00 | buf[offset]&0x00FF);
        offset += 2;
        table.cellSpacing   = (short) (buf[offset+1]<<8&0xFF00 | buf[offset]&0x00FF);
        offset += 2;
        table.inLSpace      = (short) (buf[offset+1]<<8&0xFF00 | buf[offset]&0x00FF);
        offset += 2;
        table.inRSpace      = (short) (buf[offset+1]<<8&0xFF00 | buf[offset]&0x00FF);
        offset += 2;
        table.inUSpace      = (short) (buf[offset+1]<<8&0xFF00 | buf[offset]&0x00FF);
        offset += 2;
        table.inDSpace      = (short) (buf[offset+1]<<8&0xFF00 | buf[offset]&0x00FF);
        offset += 2;
        table.rowSize = new short[table.nRows];
        for (int i=0;i<table.nRows;i++) {
            table.rowSize[i]    = (short) (buf[offset+1]<<8&0xFF00 | buf[offset]&0x00FF);
            offset += 2;
        }
        table.borderFillID  = (short) (buf[offset+1]<<8&0xFF00 | buf[offset]&0x00FF);
        offset += 2;
        if (version>=5010 && (offset-off < size)) {
            table.validZoneSize = (short) (buf[offset+1]<<8&0xFF00 | buf[offset]&0x00FF);
            offset += 2;
            if (table.validZoneSize>0 && offset-off < size) {
                table.cellzoneList = new ArrayList<CellZone>();
                for (int i=0;i<table.validZoneSize;i++) {    // 영역속성 (표 78 참조) (5.0.1.0 이상)
                    CellZone cellzone = new CellZone();
                    cellzone.startRowAddr = (short) (buf[offset+1]<<8&0xFF00 | buf[offset]&0x00FF);
                    offset += 2;
                    cellzone.startColAddr = (short) (buf[offset+1]<<8&0xFF00 | buf[offset]&0x00FF);
                    offset += 2;
                    cellzone.endRowAddr = (short) (buf[offset+1]<<8&0xFF00 | buf[offset]&0x00FF);
                    offset += 2;
                    cellzone.endColAddr = (short) (buf[offset+1]<<8&0xFF00 | buf[offset]&0x00FF);
                    offset += 2;
                    cellzone.borderFillIDRef = (short) (buf[offset+1]<<8&0xFF00 | buf[offset]&0x00FF);
                    offset += 2;
                    table.cellzoneList.add(cellzone);
                }
            }
        }
        table.cells = new ArrayList<TblCell>();
        
        log.fine("                                                  "
                +"Row수="+table.nRows
                +",Column수="+table.nCols
                +",RowSize=["+ IntStream.range(0,table.rowSize.length).mapToObj(s-> String.valueOf(table.rowSize[s])).collect(Collectors.joining(",")) +"]"
            );

        if (offset-off-size!=0) {
            log.fine("[Ctrl]= lbt, size=" + size + ", but currentSize=" + (offset-off));
            // dump(buf, off, size);
        }
        
        return offset-off;
    }
    
    public static int parseListHeaderAppend(Ctrl_Table obj, int size, byte[] buf, int off, int version) throws HwpParseException, NotImplementedException {
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
        // HWP_TABLE 이후 HWP_LIST_HEADER에 붙어 오는  24byte 또는 41byte 를 해석할 수 없다.
        return size;
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

	public static class CellZone {
	    short startRowAddr;
	    short startColAddr;
	    short endRowAddr;
	    short endColAddr;
	    short borderFillIDRef;
	}
}
