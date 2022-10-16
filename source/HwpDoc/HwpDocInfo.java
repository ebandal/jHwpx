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
package HwpDoc;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import HwpDoc.Exception.HwpParseException;
import HwpDoc.Exception.NotImplementedException;
import HwpDoc.HwpElement.HwpRecord;
import HwpDoc.HwpElement.HwpRecord_BinData;
import HwpDoc.HwpElement.HwpRecord_BorderFill;
import HwpDoc.HwpElement.HwpRecord_Bullet;
import HwpDoc.HwpElement.HwpRecord_CharShape;
import HwpDoc.HwpElement.HwpRecord_DocumentProperties;
import HwpDoc.HwpElement.HwpRecord_FaceName;
import HwpDoc.HwpElement.HwpRecord_IdMapping;
import HwpDoc.HwpElement.HwpRecord_Numbering;
import HwpDoc.HwpElement.HwpRecord_ParaShape;
import HwpDoc.HwpElement.HwpRecord_Style;
import HwpDoc.HwpElement.HwpRecord_TabDef;
import HwpDoc.HwpElement.HwpTag;
import HwpDoc.paragraph.Ctrl_PageNumPos.NumPos;
import soffice.WriterContext.HanType;

public class HwpDocInfo {
	private static final Logger log = Logger.getLogger(HwpDocInfo.class.getName());
	public HanType         hanType;
	private HwpxFile       parentHwpx;
	private HwpFile        parentHwp;
	public List<HwpRecord> recordList;
	
	public List<HwpRecord> binDataList;
	public List<HwpRecord> faceNameList;
	public List<HwpRecord> borderFillList;
	public List<HwpRecord> charShapeList;
	public List<HwpRecord> numberingList;
	public List<HwpRecord> bulletList;
	public List<HwpRecord> paraShapeList;
	public List<HwpRecord> styleList;
	public List<HwpRecord> tabDefList;
	public CompatDoc       compatibleDoc;
	
    public HwpDocInfo(HanType hanType) {
        recordList      = new ArrayList<HwpRecord>();
        binDataList     = new ArrayList<HwpRecord>();
        faceNameList    = new ArrayList<HwpRecord>();
        borderFillList  = new ArrayList<HwpRecord>();
        charShapeList   = new ArrayList<HwpRecord>();
        numberingList   = new ArrayList<HwpRecord>();
        bulletList      = new ArrayList<HwpRecord>();
        paraShapeList   = new ArrayList<HwpRecord>();
        styleList       = new ArrayList<HwpRecord>();
        tabDefList      = new ArrayList<HwpRecord>();
        compatibleDoc   = CompatDoc.HWP;
        this.hanType    = hanType; 
    }

    public HwpDocInfo(HwpxFile parent) {
        this(HanType.HWPX);
		this.parentHwpx = parent;
	}
    
    public HwpDocInfo(HwpFile parent) {
        this(HanType.HWP);
        this.parentHwp = parent;
    }
	
	boolean parse(byte[] buf, int version) throws HwpParseException, NotImplementedException {
		int off = 0;
		while(off < buf.length) {
			int header = buf[off+3]<<24&0xFF000000 | buf[off+2]<<16&0xFF0000 | buf[off+1]<<8&0xFF00 | buf[off]&0xFF;
			int tagNum = header&0x3FF;				// 10 bits (0 - 9 bit)
			int level = (header&0xFFC00)>>>10;		// 10 bits (10-19 bit)
			int size =  (header&0xFFF00000)>>>20;	// 12 bits (20-31 bit)
			
			if (size==0xFFF) {
				size = buf[off+7]<<24&0xFF000000 | buf[off+6]<<16&0xFF0000 | buf[off+5]<<8&0xFF00 | buf[off+4]&0xFF;
				off += 8;
			} else {
				off += 4;
			}
			
			HwpRecord record = null;
			HwpTag tag = HwpTag.from(tagNum);
			log.fine(IntStream.rangeClosed(0, level).mapToObj(i -> String.valueOf(i)).collect(Collectors.joining())+"[TAG]="+tag.toString()+" ("+size+")");
			switch(tag) {
			case HWPTAG_DOCUMENT_PROPERTIES:
				record = new HwpRecord_DocumentProperties(this, tagNum, level, size, buf, off, version);
				recordList.add(record);
				break;
			case HWPTAG_ID_MAPPINGS:
				record = new HwpRecord_IdMapping(this, tagNum, level, size, buf, off, version);
				recordList.add(record);
				break;
			case HWPTAG_BIN_DATA:
				record = new HwpRecord_BinData(this, tagNum, level, size, buf, off, version);
				binDataList.add(record);
				break;
			case HWPTAG_FACE_NAME:
				record = new HwpRecord_FaceName(this, tagNum, level, size, buf, off, version);
				faceNameList.add(record);
				break;
			case HWPTAG_BORDER_FILL:
				record = new HwpRecord_BorderFill(this, tagNum, level, size, buf, off, version);
				borderFillList.add(record);
				break;
			case HWPTAG_CHAR_SHAPE:
				record = new HwpRecord_CharShape(this, tagNum, level, size, buf, off, version);
				charShapeList.add(record);
				break;
			case HWPTAG_TAB_DEF:
				record = new HwpRecord_TabDef(this, tagNum, level, size, buf, off, version);
				tabDefList.add(record);
				break;
			case HWPTAG_NUMBERING:
				record = new HwpRecord_Numbering(this, tagNum, level, size, buf, off, version);
				numberingList.add(record);
				break;
			case HWPTAG_BULLET:
				record = new HwpRecord_Bullet(this, tagNum, level, size, buf, off, version);
				bulletList.add(record);
				break;
			case HWPTAG_PARA_SHAPE:
				record = new HwpRecord_ParaShape(this, tagNum, level, size, buf, off, version);
				paraShapeList.add(record);
				break;
			case HWPTAG_STYLE:
				record = new HwpRecord_Style(this, tagNum, level, size, buf, off, version);
				styleList.add(record);
				break;
            case HWPTAG_COMPATIBLE_DOCUMENT:
                compatibleDoc = CompatDoc.from(buf[off+3]<<24&0xFF000000 | buf[off+2]<<16&0x00FF0000 | buf[off+1]<<8&0x0000FF00 | buf[off]&0x000000FF);
                break;
            case HWPTAG_LAYOUT_COMPATIBILITY:
                break;
			case HWPTAG_DOC_DATA:
			case HWPTAG_DISTRIBUTE_DOC_DATA:			    
			case HWPTAG_TRACKCHANGE:
			case HWPTAG_MEMO_SHAPE:
			case HWPTAG_FORBIDDEN_CHAR:
			case HWPTAG_TRACK_CHANGE:
			case HWPTAG_TRACK_CHANGE_AUTHOR:
				break;
			default:
			}
			off += size;
		}
		
		return true;
	}
	
	boolean read(Document document, int version) throws HwpParseException, NotImplementedException {
	    int off = 0;
        
        Element element = document.getDocumentElement();
        
        System.out.println("LocalName="+element.getLocalName());
        System.out.println("NodeName="+element.getNodeName());
        System.out.println("Prefix="+element.getPrefix());
        System.out.println("TagName="+element.getTagName());    
        
        // Node : [[hh:beginNum: null], [hh:refList: null], [hh:compatibleDocument: null], [hh:docOption: null], [hh:trackchageConfig: null]]
        
        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            
            System.out.println("NodeName="+node.getNodeName());
            HwpRecord record = null;
            switch(node.getNodeName()) {
            case "hh:beginNum":
                record = new HwpRecord_DocumentProperties(this, node, version);
                recordList.add(record);
                break;
            case "hh:refList":
                readRefList(node, version);
                break;
            case "hh:compatibleDocument":
                break;
            case "hh:docOption":
                break;
            case "hh:trackchageConfig":
                break;
            case "hh:forbiddenWordList":
                break;
            }
            
        }
        
        return true;
    }
    
    private boolean readRefList(Node rootNode, int version) throws HwpParseException, NotImplementedException {
        NodeList nodeList = rootNode.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            
            HwpRecord record = null;
            switch(node.getNodeName()) {
            case "hh:fontfaces":
                {
                    NodeList children = node.getChildNodes();
                    for (int j=0; j<children.getLength(); j++) {
                        Node childNode = children.item(j);
                        record = new HwpRecord_FaceName(this, childNode, version);
                        faceNameList.add(record);
                    }
                }
                break;
            case "hh:borderFills":
                {
                    NodeList children = node.getChildNodes();
                    for (int j=0; j<children.getLength(); j++) {
                        Node childNode = children.item(j);
                        record = new HwpRecord_BorderFill(this, childNode, version);
                        borderFillList.add(record);
                    }
                }
                break;
            case "hh:charProperties":
                {
                    NodeList children = node.getChildNodes();
                    for (int j=0; j<children.getLength(); j++) {
                        Node childNode = children.item(j);
                        record = new HwpRecord_CharShape(this, childNode, version);
                        charShapeList.add(record);
                    }
                }
                break;
            case "hh:tabProperties":
                {
                    NodeList children = node.getChildNodes();
                    for (int j=0; j<children.getLength(); j++) {
                        Node childNode = children.item(j);
                        record = new HwpRecord_TabDef(this, childNode, version);
                        tabDefList.add(record);
                    }
                }
                break;
            case "hh:numberings":
                {
                    NodeList children = node.getChildNodes();
                    for (int j=0; j<children.getLength(); j++) {
                        Node childNode = children.item(j);
                        record = new HwpRecord_Numbering(this, childNode, version);
                        numberingList.add(record);
                    }
                }
                break;
            case "hh:bullets":
                record = new HwpRecord_Bullet(this, node, version);
                bulletList.add(record);
                break;
            case "hh:paraProperties":
                {
                    NodeList children = node.getChildNodes();
                    for (int j=0; j<children.getLength(); j++) {
                        Node childNode = children.item(j);
                        record = new HwpRecord_ParaShape(this, childNode, version);
                        paraShapeList.add(record);
                    }
                }
                break;
            case "hh:styles":
                {
                    NodeList children = node.getChildNodes();
                    for (int j=0; j<children.getLength(); j++) {
                        Node childNode = children.item(j);
                        record = new HwpRecord_Style(this, childNode, version);
                        styleList.add(record);
                    }
                }
                break;
            case "hh:memoProperties":
                break;
            case "hh:trackChanges":
                break;
            case "hh:trackChangeAuthros":
                break;
            }
        }
        
        return true;
    }

	public HwpFile getParentHwp() {
		return parentHwp;
	}
    
	public static enum CompatDoc {
	    HWP         (0x0),  // 한글문서(현재버전)
        OLD_HWP     (0x1),  // 한글2007호환
        MS_WORD     (0x2),  // MS 워드 호환
        ;
        
        private int num;
        private CompatDoc(int num) { 
            this.num = num;
        }
        public static CompatDoc from(int num) {
            for (CompatDoc type: values()) {
                if (type.num == num)
                    return type;
            }
            return null;
        }
	}

}
