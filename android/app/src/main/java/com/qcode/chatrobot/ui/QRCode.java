package com.qcode.chatrobot.ui;
//---------------------------------------------------------------------
// QRCode for Java
//
// Copyright (c) 2009 Kazuhiko Arase
// Copyright (c) 2017 luocf
//
// URL: http://www.d-project.com/
//
// Licensed under the MIT license:
//   http://www.opensource.org/licenses/mit-license.php
//
// The word "QR Code" is registered trademark of
// DENSO WAVE INCORPORATED
//   http://www.denso-wave.com/qrcode/faqpatent-e.html
//
//---------------------------------------------------------------------

import java.util.ArrayList;

class QRBoolean {
    public static final int FALSE = 0;
    public static final int TRUE = 1;
    public static final int INVALID = -1;
    public static int Convert(boolean b) {
        if (b) {
            return TRUE;
        } else {
            return FALSE;
        }
    }
    public static boolean ConvertToBool(int i) {
        if (i == 1) {
            return true;
        } else {
            return false;
        }
    }
};

//---------------------------------------------------------------------
// QR8bitByte
//---------------------------------------------------------------------

class QRMode {
    public static final int MODE_NUMBER = 1 << 0;
    public static final int MODE_ALPHA_NUM = 1 << 1;
    public static final int MODE_8BIT_BYTE = 1 << 2;
    public static final int MODE_KANJI = 1 << 3;
}

//---------------------------------------------------------------------
// QRErrorCorrectLevel
//---------------------------------------------------------------------
class QRErrorCorrectLevel{
    public final static int L = 1;
    public final static int M = 0;
    public final static int Q = 3;
    public final static int H = 2;
};

//---------------------------------------------------------------------
// QRMaskPattern
//---------------------------------------------------------------------
class QRMaskPattern {
    public final static int PATTERN000 = 0;
    public final static int PATTERN001 = 1;
    public final static int PATTERN010 = 2;
    public final static int PATTERN011 = 3;
    public final static int PATTERN100 = 4;
    public final static int PATTERN101 = 5;
    public final static int PATTERN110 = 6;
    public final static int PATTERN111 = 7;
};
//---------------------------------------------------------------------
// QRMath
//---------------------------------------------------------------------

class QRMath{
    static public int glog(int n) {

        if (n < 1) {
            throw new Error("glog(" + n + ")");
        }
        return QRMath.LOG_TABLE[n];
    };

    static public int gexp(int n) {
        while (n < 0) {
            n += 255;
        }

        while (n >= 256) {
            n -= 255;
        }
        return QRMath.EXP_TABLE[n];
    };

    static public int [] EXP_TABLE = new int[256];
    static public int [] LOG_TABLE = new int[256];
    static public void Init() {
        for (int i = 0; i < 8; i++) {
            QRMath.EXP_TABLE[i] = 1 << i;
        }
        for (int i = 8; i < 256; i++) {
            QRMath.EXP_TABLE[i] = QRMath.EXP_TABLE[i - 4]
                    ^ QRMath.EXP_TABLE[i - 5]
                    ^ QRMath.EXP_TABLE[i - 6]
                    ^ QRMath.EXP_TABLE[i - 8];
        }
        for (int i = 0; i < 255; i++) {
            QRMath.LOG_TABLE[QRMath.EXP_TABLE[i] ] = i;
        }
    }
};

//---------------------------------------------------------------------
// QRPolynomial
//---------------------------------------------------------------------
class QRPolynomial{
    public int[] num;
    QRPolynomial(int [] num, int shift) {
        if (num.length == 0) {
            throw new Error(num.length + "/" + shift);
        }
        int offset = 0;

        while (offset < num.length && num[offset] == 0) {
            offset++;
        }

        this.num = new int[num.length - offset + shift];
        for (int i = 0; i < num.length - offset; i++) {
            this.num[i] = num[i + offset];
        }
    };

    public int get(int index) {
        return this.num[index];
    };

    public int  getLength() {
        return this.num.length;
    };
    public QRPolynomial multiply(QRPolynomial e) {
        int [] num = new int[this.getLength() + e.getLength() - 1];
        for (int i = 0; i < this.getLength(); i++) {
            for (int j = 0; j < e.getLength(); j++) {
                num[i + j] ^= QRMath.gexp(QRMath.glog(this.get(i) ) + QRMath.glog(e.get(j) ) );
            }
        }

        return new QRPolynomial(num, 0);
    };
    public QRPolynomial mod(QRPolynomial e) {

        if (this.getLength() - e.getLength() < 0) {
            return this;
        }

        int ratio = QRMath.glog(this.get(0) ) - QRMath.glog(e.get(0) );

        int[] num = new int[this.getLength()];

        for (int i = 0; i < this.getLength(); i++) {
            num[i] = this.get(i);
        }

        for (int i = 0; i < e.getLength(); i++) {
            num[i] ^= QRMath.gexp(QRMath.glog(e.get(i) ) + ratio);
        }

        // recursive call
        return new QRPolynomial(num, 0).mod(e);
    }
};

class QRUtil {

    static public int[][] PATTERN_POSITION_TABLE = {
            {},
            {6, 18},
            {6, 22},
            {6, 26},
            {6, 30},
            {6, 34},
            {6, 22, 38},
            {6, 24, 42},
            {6, 26, 46},
            {6, 28, 50},
            {6, 30, 54},
            {6, 32, 58},
            {6, 34, 62},
            {6, 26, 46, 66},
            {6, 26, 48, 70},
            {6, 26, 50, 74},
            {6, 30, 54, 78},
            {6, 30, 56, 82},
            {6, 30, 58, 86},
            {6, 34, 62, 90},
            {6, 28, 50, 72, 94},
            {6, 26, 50, 74, 98},
            {6, 30, 54, 78, 102},
            {6, 28, 54, 80, 106},
            {6, 32, 58, 84, 110},
            {6, 30, 58, 86, 114},
            {6, 34, 62, 90, 118},
            {6, 26, 50, 74, 98, 122},
            {6, 30, 54, 78, 102, 126},
            {6, 26, 52, 78, 104, 130},
            {6, 30, 56, 82, 108, 134},
            {6, 34, 60, 86, 112, 138},
            {6, 30, 58, 86, 114, 142},
            {6, 34, 62, 90, 118, 146},
            {6, 30, 54, 78, 102, 126, 150},
            {6, 24, 50, 76, 102, 128, 154},
            {6, 28, 54, 80, 106, 132, 158},
            {6, 32, 58, 84, 110, 136, 162},
            {6, 26, 54, 82, 110, 138, 166},
            {6, 30, 58, 86, 114, 142, 170}
    };

    static final int G15 = (1 << 10) | (1 << 8) | (1 << 5) | (1 << 4) | (1 << 2) | (1 << 1) | (1 << 0);
    static final int G18 =  (1 << 12) | (1 << 11) | (1 << 10) | (1 << 9) | (1 << 8) | (1 << 5) | (1 << 2) | (1 << 0);
    static final int G15_MASK =  (1 << 14) | (1 << 12) | (1 << 10)	| (1 << 4) | (1 << 1);

    static int getBCHTypeInfo (int data) {
        int d = data << 10;
        while (QRUtil.getBCHDigit(d) - QRUtil.getBCHDigit(QRUtil.G15) >= 0) {
            d ^= (QRUtil.G15 << (QRUtil.getBCHDigit(d) - QRUtil.getBCHDigit(QRUtil.G15) ) );
        }
        return ( (data << 10) | d) ^ QRUtil.G15_MASK;
    };

    static int getBCHTypeNumber(int data) {
        int d = data << 12;
        while (QRUtil.getBCHDigit(d) - QRUtil.getBCHDigit(QRUtil.G18) >= 0) {
            d ^= (QRUtil.G18 << (QRUtil.getBCHDigit(d) - QRUtil.getBCHDigit(QRUtil.G18) ) );
        }
        return (data << 12) | d;
    };

    static int getBCHDigit(int data) {
        int digit = 0;
        while (data != 0) {
            digit++;
            data >>>= 1;
        }
        return digit;
    };

    static int[] getPatternPosition(int typeNumber) {
        return QRUtil.PATTERN_POSITION_TABLE[typeNumber - 1];
    };

    static boolean getMask(final int maskPattern, int i, int j) {

        switch (maskPattern) {
            case QRMaskPattern.PATTERN000:
                return (i + j) % 2 == 0;
            case QRMaskPattern.PATTERN001:
                return i % 2 == 0;
            case QRMaskPattern.PATTERN010:
                return j % 3 == 0;
            case QRMaskPattern.PATTERN011:
                return (i + j) % 3 == 0;
            case QRMaskPattern.PATTERN100 :
                return (Math.floor(i / 2) + Math.floor(j / 3) ) % 2 == 0;
            case QRMaskPattern.PATTERN101 :
                return (i * j) % 2 + (i * j) % 3 == 0;
            case QRMaskPattern.PATTERN110 :
                return ( (i * j) % 2 + (i * j) % 3) % 2 == 0;
            case QRMaskPattern.PATTERN111 :
                return ( (i * j) % 3 + (i + j) % 2) % 2 == 0;
            default :
                throw new Error("bad maskPattern:" + maskPattern);
        }
    };

    static public QRPolynomial getErrorCorrectPolynomial(int errorCorrectLength) {
        int[] num = {1};
        QRPolynomial a = new QRPolynomial(num, 0);
        for (int i = 0; i < errorCorrectLength; i++) {
            int[] val = {1, QRMath.gexp(i)};
            a = a.multiply(new QRPolynomial(val, 0) );
        }
        return a;
    };

    static public int getLengthInBits(int mode, int type) {

        if (1 <= type && type < 10) {
            // 1 - 9
            switch(mode) {
                case QRMode.MODE_NUMBER 	: return 10;
                case QRMode.MODE_ALPHA_NUM 	: return 9;
                case QRMode.MODE_8BIT_BYTE	: return 8;
                case QRMode.MODE_KANJI  	: return 8;
                default :
                    throw new Error("mode:" + mode);
            }
        } else if (type < 27) {
            // 10 - 26
            switch(mode) {
                case QRMode.MODE_NUMBER 	: return 12;
                case QRMode.MODE_ALPHA_NUM 	: return 11;
                case QRMode.MODE_8BIT_BYTE	: return 16;
                case QRMode.MODE_KANJI  	: return 10;
                default :
                    throw new Error("mode:" + mode);
            }
        } else if (type < 41) {
            // 27 - 40
            switch(mode) {
                case QRMode.MODE_NUMBER 	: return 14;
                case QRMode.MODE_ALPHA_NUM	: return 13;
                case QRMode.MODE_8BIT_BYTE	: return 16;
                case QRMode.MODE_KANJI  	: return 12;
                default :
                    throw new Error("mode:" + mode);
            }
        } else {
            throw new Error("type:" + type);
        }
    };

    static public int getLostPoint(QRCode qrCode) {
        int moduleCount = qrCode.getModuleCount();
        int lostPoint = 0;
        // LEVEL1
        for (int row = 0; row < moduleCount; row++) {
            for (int col = 0; col < moduleCount; col++) {

                int sameCount = 0;
                boolean dark = qrCode.isDark(row, col);
                for (int r = -1; r <= 1; r++) {
                    if (row + r < 0 || moduleCount <= row + r) {
                        continue;
                    }

                    for (int c = -1; c <= 1; c++) {

                        if (col + c < 0 || moduleCount <= col + c) {
                            continue;
                        }

                        if (r == 0 && c == 0) {
                            continue;
                        }

                        if (dark == qrCode.isDark(row + r, col + c) ) {
                            sameCount++;
                        }
                    }
                }
                if (sameCount > 5) {
                    lostPoint += (3 + sameCount - 5);
                }
            }
        }
        // LEVEL2
        for (int row = 0; row < moduleCount - 1; row++) {
            for (int col = 0; col < moduleCount - 1; col++) {
                int count = 0;
                if (qrCode.isDark(row,     col    ) ) count++;
                if (qrCode.isDark(row + 1, col    ) ) count++;
                if (qrCode.isDark(row,     col + 1) ) count++;
                if (qrCode.isDark(row + 1, col + 1) ) count++;
                if (count == 0 || count == 4) {
                    lostPoint += 3;
                }
            }
        }

        // LEVEL3
        for (int row = 0; row < moduleCount; row++) {
            for (int col = 0; col < moduleCount - 6; col++) {
                if (qrCode.isDark(row, col)
                        && !qrCode.isDark(row, col + 1)
                        &&  qrCode.isDark(row, col + 2)
                        &&  qrCode.isDark(row, col + 3)
                        &&  qrCode.isDark(row, col + 4)
                        && !qrCode.isDark(row, col + 5)
                        &&  qrCode.isDark(row, col + 6) ) {
                    lostPoint += 40;
                }
            }
        }

        for (int col = 0; col < moduleCount; col++) {
            for (int row = 0; row < moduleCount - 6; row++) {
                if (qrCode.isDark(row, col)
                        && !qrCode.isDark(row + 1, col)
                        &&  qrCode.isDark(row + 2, col)
                        &&  qrCode.isDark(row + 3, col)
                        &&  qrCode.isDark(row + 4, col)
                        && !qrCode.isDark(row + 5, col)
                        &&  qrCode.isDark(row + 6, col) ) {
                    lostPoint += 40;
                }
            }
        }
        // LEVEL4
        int darkCount = 0;
        for (int col = 0; col < moduleCount; col++) {
            for (int row = 0; row < moduleCount; row++) {
                if (qrCode.isDark(row, col) ) {
                    darkCount++;
                }
            }
        }
        int ratio = Math.abs(100 * darkCount / moduleCount / moduleCount - 50) / 5;
        lostPoint += ratio * 10;

        return lostPoint;
    }

};

class QRBitBuffer {
    public ArrayList<Integer> buffer;
    public int length;
    QRBitBuffer() {
        this.buffer = new ArrayList<Integer>();
        this.length = 0;
    }

    public boolean get(int index) {
        int bufIndex = (int)(index / 8);
        int value = this.buffer.get(bufIndex);
        return ((value >>> (7 - index % 8) ) & 1) == 1;
    };

    public int getLengthInBits() {
        return this.length;
    }
    public void put(int num, int length) {
        for (int i = 0; i < length; i++) {
            this.putBit(( (num >>> (length - i - 1) ) & 1) == 1);
        }
    }
    public void putBit(boolean bit) {

        int bufIndex = (int)(this.length / 8);
        int size = this.buffer.size();
        if (size <= bufIndex) {
            this.buffer.add(0);
        }

        if (bit) {
            int value = this.buffer.get(bufIndex);
            value |= (0x80 >>> (this.length % 8) );
            this.buffer.set(bufIndex, value);
        }

        this.length++;
    }
}

class QR8bitByte {
    public String data;
    public int mode;
    QR8bitByte(String data) {
        this.data = data;
        this.mode = QRMode.MODE_8BIT_BYTE;
    }
    public int getLength() {
        return this.data.length();
    }
    public void write(QRBitBuffer buffer) {
        for (int i = 0; i < this.data.length(); i++) {
            // not JIS ...
            buffer.put((this.data.codePointAt(i)), 8);
        }
    }
}

//---------------------------------------------------------------------
// QRRSBlock
//---------------------------------------------------------------------
class QRRSBlock {
    public int totalCount;
    public int dataCount;
    QRRSBlock(int totalCount, int dataCount) {
        this.totalCount = totalCount;
        this.dataCount  = dataCount;
    }
    static public int[][] RS_BLOCK_TABLE = {
            // L
            // M
            // Q
            // H

            // 1
            {1, 26, 19},
            {1, 26, 16},
            {1, 26, 13},
            {1, 26, 9},

            // 2
            {1, 44, 34},
            {1, 44, 28},
            {1, 44, 22},
            {1, 44, 16},

            // 3
            {1, 70, 55},
            {1, 70, 44},
            {2, 35, 17},
            {2, 35, 13},

            // 4
            {1, 100, 80},
            {2, 50, 32},
            {2, 50, 24},
            {4, 25, 9},

            // 5
            {1, 134, 108},
            {2, 67, 43},
            {2, 33, 15, 2, 34, 16},
            {2, 33, 11, 2, 34, 12},

            // 6
            {2, 86, 68},
            {4, 43, 27},
            {4, 43, 19},
            {4, 43, 15},

            // 7
            {2, 98, 78},
            {4, 49, 31},
            {2, 32, 14, 4, 33, 15},
            {4, 39, 13, 1, 40, 14},

            // 8
            {2, 121, 97},
            {2, 60, 38, 2, 61, 39},
            {4, 40, 18, 2, 41, 19},
            {4, 40, 14, 2, 41, 15},

            // 9
            {2, 146, 116},
            {3, 58, 36, 2, 59, 37},
            {4, 36, 16, 4, 37, 17},
            {4, 36, 12, 4, 37, 13},

            // 10
            {2, 86, 68, 2, 87, 69},
            {4, 69, 43, 1, 70, 44},
            {6, 43, 19, 2, 44, 20},
            {6, 43, 15, 2, 44, 16},

            // 11
            {4, 101, 81},
            {1, 80, 50, 4, 81, 51},
            {4, 50, 22, 4, 51, 23},
            {3, 36, 12, 8, 37, 13},

            // 12
            {2, 116, 92, 2, 117, 93},
            {6, 58, 36, 2, 59, 37},
            {4, 46, 20, 6, 47, 21},
            {7, 42, 14, 4, 43, 15},

            // 13
            {4, 133, 107},
            {8, 59, 37, 1, 60, 38},
            {8, 44, 20, 4, 45, 21},
            {12, 33, 11, 4, 34, 12},

            // 14
            {3, 145, 115, 1, 146, 116},
            {4, 64, 40, 5, 65, 41},
            {11, 36, 16, 5, 37, 17},
            {11, 36, 12, 5, 37, 13},

            // 15
            {5, 109, 87, 1, 110, 88},
            {5, 65, 41, 5, 66, 42},
            {5, 54, 24, 7, 55, 25},
            {11, 36, 12},

            // 16
            {5, 122, 98, 1, 123, 99},
            {7, 73, 45, 3, 74, 46},
            {15, 43, 19, 2, 44, 20},
            {3, 45, 15, 13, 46, 16},

            // 17
            {1, 135, 107, 5, 136, 108},
            {10, 74, 46, 1, 75, 47},
            {1, 50, 22, 15, 51, 23},
            {2, 42, 14, 17, 43, 15},

            // 18
            {5, 150, 120, 1, 151, 121},
            {9, 69, 43, 4, 70, 44},
            {17, 50, 22, 1, 51, 23},
            {2, 42, 14, 19, 43, 15},

            // 19
            {3, 141, 113, 4, 142, 114},
            {3, 70, 44, 11, 71, 45},
            {17, 47, 21, 4, 48, 22},
            {9, 39, 13, 16, 40, 14},

            // 20
            {3, 135, 107, 5, 136, 108},
            {3, 67, 41, 13, 68, 42},
            {15, 54, 24, 5, 55, 25},
            {15, 43, 15, 10, 44, 16},

            // 21
            {4, 144, 116, 4, 145, 117},
            {17, 68, 42},
            {17, 50, 22, 6, 51, 23},
            {19, 46, 16, 6, 47, 17},

            // 22
            {2, 139, 111, 7, 140, 112},
            {17, 74, 46},
            {7, 54, 24, 16, 55, 25},
            {34, 37, 13},

            // 23
            {4, 151, 121, 5, 152, 122},
            {4, 75, 47, 14, 76, 48},
            {11, 54, 24, 14, 55, 25},
            {16, 45, 15, 14, 46, 16},

            // 24
            {6, 147, 117, 4, 148, 118},
            {6, 73, 45, 14, 74, 46},
            {11, 54, 24, 16, 55, 25},
            {30, 46, 16, 2, 47, 17},

            // 25
            {8, 132, 106, 4, 133, 107},
            {8, 75, 47, 13, 76, 48},
            {7, 54, 24, 22, 55, 25},
            {22, 45, 15, 13, 46, 16},

            // 26
            {10, 142, 114, 2, 143, 115},
            {19, 74, 46, 4, 75, 47},
            {28, 50, 22, 6, 51, 23},
            {33, 46, 16, 4, 47, 17},

            // 27
            {8, 152, 122, 4, 153, 123},
            {22, 73, 45, 3, 74, 46},
            {8, 53, 23, 26, 54, 24},
            {12, 45, 15, 28, 46, 16},

            // 28
            {3, 147, 117, 10, 148, 118},
            {3, 73, 45, 23, 74, 46},
            {4, 54, 24, 31, 55, 25},
            {11, 45, 15, 31, 46, 16},

            // 29
            {7, 146, 116, 7, 147, 117},
            {21, 73, 45, 7, 74, 46},
            {1, 53, 23, 37, 54, 24},
            {19, 45, 15, 26, 46, 16},

            // 30
            {5, 145, 115, 10, 146, 116},
            {19, 75, 47, 10, 76, 48},
            {15, 54, 24, 25, 55, 25},
            {23, 45, 15, 25, 46, 16},

            // 31
            {13, 145, 115, 3, 146, 116},
            {2, 74, 46, 29, 75, 47},
            {42, 54, 24, 1, 55, 25},
            {23, 45, 15, 28, 46, 16},

            // 32
            {17, 145, 115},
            {10, 74, 46, 23, 75, 47},
            {10, 54, 24, 35, 55, 25},
            {19, 45, 15, 35, 46, 16},

            // 33
            {17, 145, 115, 1, 146, 116},
            {14, 74, 46, 21, 75, 47},
            {29, 54, 24, 19, 55, 25},
            {11, 45, 15, 46, 46, 16},

            // 34
            {13, 145, 115, 6, 146, 116},
            {14, 74, 46, 23, 75, 47},
            {44, 54, 24, 7, 55, 25},
            {59, 46, 16, 1, 47, 17},

            // 35
            {12, 151, 121, 7, 152, 122},
            {12, 75, 47, 26, 76, 48},
            {39, 54, 24, 14, 55, 25},
            {22, 45, 15, 41, 46, 16},

            // 36
            {6, 151, 121, 14, 152, 122},
            {6, 75, 47, 34, 76, 48},
            {46, 54, 24, 10, 55, 25},
            {2, 45, 15, 64, 46, 16},

            // 37
            {17, 152, 122, 4, 153, 123},
            {29, 74, 46, 14, 75, 47},
            {49, 54, 24, 10, 55, 25},
            {24, 45, 15, 46, 46, 16},

            // 38
            {4, 152, 122, 18, 153, 123},
            {13, 74, 46, 32, 75, 47},
            {48, 54, 24, 14, 55, 25},
            {42, 45, 15, 32, 46, 16},

            // 39
            {20, 147, 117, 4, 148, 118},
            {40, 75, 47, 7, 76, 48},
            {43, 54, 24, 22, 55, 25},
            {10, 45, 15, 67, 46, 16},

            // 40
            {19, 148, 118, 6, 149, 119},
            {18, 75, 47, 31, 76, 48},
            {34, 54, 24, 34, 55, 25},
            {20, 45, 15, 61, 46, 16}
    };
    static public ArrayList<QRRSBlock> getRSBlocks(int typeNumber, int errorCorrectLevel) {
        int[] rsBlock = QRRSBlock.getRsBlockTable(typeNumber, errorCorrectLevel);
        if (rsBlock == null) {
            throw new Error("bad rs block @ typeNumber:" + typeNumber + "/errorCorrectLevel:" + errorCorrectLevel);
        }
        int length = rsBlock.length / 3;

        ArrayList<QRRSBlock> list = new ArrayList<QRRSBlock>();

        for (int i = 0; i < length; i++) {

            int count = rsBlock[i * 3 + 0];
            int totalCount = rsBlock[i * 3 + 1];
            int dataCount  = rsBlock[i * 3 + 2];

            for (int j = 0; j < count; j++) {
                list.add(new QRRSBlock(totalCount, dataCount) );
            }
        }

        return list;
    };

    static public int[] getRsBlockTable(int typeNumber, int errorCorrectLevel) {
        switch(errorCorrectLevel) {
            case QRErrorCorrectLevel.L :
                return QRRSBlock.RS_BLOCK_TABLE[(typeNumber - 1) * 4 + 0];
            case QRErrorCorrectLevel.M :
                return QRRSBlock.RS_BLOCK_TABLE[(typeNumber - 1) * 4 + 1];
            case QRErrorCorrectLevel.Q :
                return QRRSBlock.RS_BLOCK_TABLE[(typeNumber - 1) * 4 + 2];
            case QRErrorCorrectLevel.H :
                return QRRSBlock.RS_BLOCK_TABLE[(typeNumber - 1) * 4 + 3];
            default :
                return null;
        }
    };

}


public class QRCode {
    private int typeNumber;
    private int errorCorrectLevel;
    private int moduleCount;
    private ArrayList<QR8bitByte> dataList;
    private int[] dataCache;
    private int [][] modules;//-1:invalid , 0:false, 1:true
    public static int PAD0 = 0xEC;
    public static int PAD1 = 0x11;

    public QRCode(int typeNumber, int errorCorrectLevel) {
        this.typeNumber = typeNumber;
        this.errorCorrectLevel = errorCorrectLevel;
        this.modules = null;
        this.moduleCount = 0;
        this.dataCache = null;
        this.dataList = new ArrayList<QR8bitByte>();
        QRMath.Init();
    }

    public void addData(String data) {
        QR8bitByte newData = new QR8bitByte(data);
        this.dataList.add(newData);
        this.dataCache = null;
    };

    public boolean isDark (int row, int col) {
        if (row < 0 || this.moduleCount <= row || col < 0 || this.moduleCount <= col) {
            throw new Error(row + "," + col);
        }
        return QRBoolean.ConvertToBool(this.modules[row][col]);
    };

    public int getModuleCount() {
        return this.moduleCount;
    };

    public void make() {
        // Calculate automatically typeNumber if provided is < 1
        if (this.typeNumber < 1 ){
            int typeNumber = 1;
            for (typeNumber = 1; typeNumber < 40; typeNumber++) {
                ArrayList<QRRSBlock> rsBlocks = QRRSBlock.getRSBlocks(typeNumber, this.errorCorrectLevel);

                QRBitBuffer buffer = new QRBitBuffer();
                int totalDataCount = 0;
                for (int i = 0; i < rsBlocks.size(); i++) {
                    totalDataCount += rsBlocks.get(i).dataCount;
                }

                for (int i = 0; i < this.dataList.size(); i++) {
                    QR8bitByte data = this.dataList.get(i);
                    buffer.put(data.mode, 4);
                    buffer.put(data.getLength(), QRUtil.getLengthInBits(data.mode, typeNumber) );
                    data.write(buffer);
                }
                if (buffer.getLengthInBits() <= totalDataCount * 8)
                    break;
            }
            this.typeNumber = typeNumber;
        }
        this.makeImpl(false, this.getBestMaskPattern() );
    };

    public void makeImpl(boolean test, int maskPattern) {

        this.moduleCount = this.typeNumber * 4 + 17;
        this.modules = new int[this.moduleCount][];

        for (int row = 0; row < this.moduleCount; row++) {
            this.modules[row] = new int[this.moduleCount];
            for (int col = 0; col < this.moduleCount; col++) {
                this.modules[row][col] = QRBoolean.INVALID;;//null //(col + row) % 3;
            }
        }

        this.setupPositionProbePattern(0, 0);
        this.setupPositionProbePattern(this.moduleCount - 7, 0);
        this.setupPositionProbePattern(0, this.moduleCount - 7);
        this.setupPositionAdjustPattern();
        this.setupTimingPattern();
        this.setupTypeInfo(test, maskPattern);

        if (this.typeNumber >= 7) {
            this.setupTypeNumber(test);
        }

        if (this.dataCache == null) {
            this.dataCache = QRCode.createData(this.typeNumber, this.errorCorrectLevel, this.dataList);
        }

        this.mapData(this.dataCache, maskPattern);
    };

    public void setupPositionProbePattern(int row, int col)  {

        for (int r = -1; r <= 7; r++) {

            if (row + r <= -1 || this.moduleCount <= row + r) continue;

            for (int c = -1; c <= 7; c++) {

                if (col + c <= -1 || this.moduleCount <= col + c) continue;

                if ( (0 <= r && r <= 6 && (c == 0 || c == 6) )
                        || (0 <= c && c <= 6 && (r == 0 || r == 6) )
                        || (2 <= r && r <= 4 && 2 <= c && c <= 4) ) {
                    this.modules[row + r][col + c] = QRBoolean.TRUE;;
                } else {
                    this.modules[row + r][col + c] = QRBoolean.FALSE;;
                }
            }
        }
    };

    public int getBestMaskPattern() {

        int minLostPoint = 0;
        int pattern = 0;

        for (int i = 0; i < 8; i++) {

            this.makeImpl(true, i);

            int lostPoint = QRUtil.getLostPoint(this);

            if (i == 0 || minLostPoint >  lostPoint) {
                minLostPoint = lostPoint;
                pattern = i;
            }
        }

        return pattern;
    };
    public void setupTimingPattern() {

        for (int r = 8; r < this.moduleCount - 8; r++) {
            if (this.modules[r][6] != QRBoolean.INVALID) {
                continue;
            }
            this.modules[r][6] = QRBoolean.Convert(r % 2 == 0);

        }

        for (int c = 8; c < this.moduleCount - 8; c++) {
            if (this.modules[6][c] != QRBoolean.INVALID) {
                continue;
            }
            this.modules[6][c] = QRBoolean.Convert(c % 2 == 0);
        }
    };

    public void  setupPositionAdjustPattern() {

        int[] pos = QRUtil.getPatternPosition(this.typeNumber);

        for (int i = 0; i < pos.length; i++) {

            for (int j = 0; j < pos.length; j++) {

                int row = pos[i];
                int col = pos[j];

                if (this.modules[row][col] != QRBoolean.INVALID) {
                    continue;
                }

                for (int r = -2; r <= 2; r++) {

                    for (int c = -2; c <= 2; c++) {

                        if (r == -2 || r == 2 || c == -2 || c == 2
                                || (r == 0 && c == 0) ) {
                            this.modules[row + r][col + c] = QRBoolean.TRUE;
                        } else {
                            this.modules[row + r][col + c] = QRBoolean.FALSE;
                        }
                    }
                }
            }
        }
    };

    public void setupTypeNumber(boolean test) {

        int bits = QRUtil.getBCHTypeNumber(this.typeNumber);

        for (int i = 0; i < 18; i++) {
            int mod =  QRBoolean.Convert(!test && ( (bits >> i) & 1) == 1) ;
            this.modules[(int) Math.floor(i / 3)][i % 3 + this.moduleCount - 8 - 3] = mod;
        }

        for (int i = 0; i < 18; i++) {
            int mod =  QRBoolean.Convert(!test && ( (bits >> i) & 1) == 1);
            this.modules[i % 3 + this.moduleCount - 8 - 3][(int) Math.floor(i / 3)] = mod;
        }
    };

    public void setupTypeInfo(boolean test, int maskPattern) {

        int data = (this.errorCorrectLevel << 3) | maskPattern;
        int bits = QRUtil.getBCHTypeInfo(data);

        // vertical
        for (int i = 0; i < 15; i++) {

            int mod =  QRBoolean.Convert(!test && ( (bits >> i) & 1) == 1);

            if (i < 6) {
                this.modules[i][8] = mod;
            } else if (i < 8) {
                this.modules[i + 1][8] = mod;
            } else {
                this.modules[this.moduleCount - 15 + i][8] = mod;
            }
        }

        // horizontal
        for (int i = 0; i < 15; i++) {

            int mod = QRBoolean.Convert(!test && ( (bits >> i) & 1) == 1);

            if (i < 8) {
                this.modules[8][this.moduleCount - i - 1] = mod;
            } else if (i < 9) {
                this.modules[8][15 - i - 1 + 1] = mod;
            } else {
                this.modules[8][15 - i - 1] = mod;
            }
        }
        // fixed module
        this.modules[this.moduleCount - 8][8] = QRBoolean.Convert(!test);

    };

    public void mapData(int [] data, int maskPattern) {

        int inc = -1;
        int row = this.moduleCount - 1;
        int bitIndex = 7;
        int byteIndex = 0;

        for (int col = this.moduleCount - 1; col > 0; col -= 2) {

            if (col == 6) col--;

            while (true) {

                for (int c = 0; c < 2; c++) {

                    if (this.modules[row][col - c] == QRBoolean.INVALID) {

                        boolean dark = false;

                        if (byteIndex < data.length) {
                            dark = ( ( (data[byteIndex] >>> bitIndex) & 1) == 1);
                        }

                        boolean mask = QRUtil.getMask(maskPattern, row, col - c);

                        if (mask) {
                            dark = !dark;
                        }

                        this.modules[row][col - c] = QRBoolean.Convert(dark);
                        bitIndex--;

                        if (bitIndex == -1) {
                            byteIndex++;
                            bitIndex = 7;
                        }
                    }
                }

                row += inc;

                if (row < 0 || this.moduleCount <= row) {
                    row -= inc;
                    inc = -inc;
                    break;
                }
            }
        }
    };

    static public int[] createData(int typeNumber, int errorCorrectLevel, ArrayList<QR8bitByte> dataList) {

        ArrayList<QRRSBlock> rsBlocks = QRRSBlock.getRSBlocks(typeNumber, errorCorrectLevel);

        QRBitBuffer buffer = new QRBitBuffer();

        for (int i = 0; i < dataList.size(); i++) {
            QR8bitByte data = dataList.get(i);
            buffer.put(data.mode, 4);
            buffer.put(data.getLength(), QRUtil.getLengthInBits(data.mode, typeNumber) );
            data.write(buffer);
        }

        // calc num max data.
        int totalDataCount = 0;
        for (int i = 0; i < rsBlocks.size(); i++) {
            totalDataCount += rsBlocks.get(i).dataCount;
        }

        if (buffer.getLengthInBits() > totalDataCount * 8) {
            throw new Error("code length overflow. ("
                    + buffer.getLengthInBits()
                    + ">"
                    +  totalDataCount * 8
                    + ")");
        }

        // end code
        if (buffer.getLengthInBits() + 4 <= totalDataCount * 8) {
            buffer.put(0, 4);
        }

        // padding
        while (buffer.getLengthInBits() % 8 != 0) {
            buffer.putBit(false);
        }

        // padding
        while (true) {

            if (buffer.getLengthInBits() >= totalDataCount * 8) {
                break;
            }
            buffer.put(QRCode.PAD0, 8);

            if (buffer.getLengthInBits() >= totalDataCount * 8) {
                break;
            }
            buffer.put(QRCode.PAD1, 8);
        }

        return QRCode.createBytes(buffer, rsBlocks);
    }

    static public int[] createBytes(QRBitBuffer buffer, ArrayList<QRRSBlock> rsBlocks) {

        int offset = 0;

        int maxDcCount = 0;
        int maxEcCount = 0;

        int[][] dcdata = new int[rsBlocks.size()][];
        int[][] ecdata = new int[rsBlocks.size()][];

        for (int r = 0; r < rsBlocks.size(); r++) {

            int dcCount = rsBlocks.get(r).dataCount;
            int ecCount = rsBlocks.get(r).totalCount - dcCount;

            maxDcCount = Math.max(maxDcCount, dcCount);
            maxEcCount = Math.max(maxEcCount, ecCount);

            dcdata[r] = new int[dcCount];

            for (int i = 0; i < dcdata[r].length; i++) {
                dcdata[r][i] = 0xff & buffer.buffer.get(i + offset);
            }
            offset += dcCount;

            QRPolynomial rsPoly = QRUtil.getErrorCorrectPolynomial(ecCount);
            QRPolynomial rawPoly = new QRPolynomial(dcdata[r], rsPoly.getLength() - 1);

            QRPolynomial modPoly = rawPoly.mod(rsPoly);
            ecdata[r] = new int[rsPoly.getLength() - 1];
            for (int i = 0; i < ecdata[r].length; i++) {
                int modIndex = i + modPoly.getLength() - ecdata[r].length;
                ecdata[r][i] = (modIndex >= 0)? modPoly.get(modIndex) : 0;
            }

        }

        int totalCodeCount = 0;
        for (int i = 0; i < rsBlocks.size(); i++) {
            totalCodeCount += rsBlocks.get(i).totalCount;
        }

        int[] data = new int[totalCodeCount];
        int index = 0;

        for (int i = 0; i < maxDcCount; i++) {
            for (int r = 0; r < rsBlocks.size(); r++) {
                if (i < dcdata[r].length) {
                    data[index++] = dcdata[r][i];
                }
            }
        }

        for (int i = 0; i < maxEcCount; i++) {
            for (int r = 0; r < rsBlocks.size(); r++) {
                if (i < ecdata[r].length) {
                    data[index++] = ecdata[r][i];
                }
            }
        }
        return data;
    }
}