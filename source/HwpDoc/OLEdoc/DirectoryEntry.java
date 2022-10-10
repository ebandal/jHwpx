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
 * 작성자 : 반희수 ebandal@gmail.com  
 * 작성일 : 2022.10
 */
package HwpDoc.OLEdoc;

import java.util.List;

public class DirectoryEntry {
	String directoryEntryName;
	int objectType;
	int colorFlag;
	int leftSiblingID;
	int rightSiblingID;
	int childID;
	long clsID1;
	long clsID2;
	int stateBit;
	long creationTime;
	long modifiedTime;
	int startingSectorID;
	long streamSize;
	List<Integer> secNums;
	
	public DirectoryEntry(String directoryEntryName, int objectType, int colorFlag, int leftSiblingID, int rightSiblingID, int childID, 
							long clsID1, long clsID2, int stateBit, long creationTime, long modifiedTime, int startingSectorID, long streamSize) { 
		this.directoryEntryName = directoryEntryName;
		this.objectType = objectType;
		this.colorFlag = colorFlag;
		this.leftSiblingID = leftSiblingID;
		this.rightSiblingID = rightSiblingID;
		this.childID = childID;
		this.clsID1 = clsID1;
		this.clsID2 = clsID2;
		this.stateBit = stateBit;
		this.creationTime = creationTime;
		this.modifiedTime = modifiedTime;
		this.startingSectorID = startingSectorID;
		this.streamSize = streamSize;
	}

	public int getObjectType() {
		return objectType;
	}

	public String getDirectoryEntryName() {
		return directoryEntryName;
	}
}