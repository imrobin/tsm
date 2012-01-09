package com.em;
/*
 * author: 		guizy
 * Date:		2008-06-24
 * Last Modify:	2008-06-24
 */
public class EMResult {
        private int     recode=0;
        private byte[] 	data=null;
        private byte[] 	mac=null;
        private byte[] 	criperData=null;
        private int	length=0;
        private byte[] 	kek=null;
        private byte[] 	lmkkek=null;
        private byte[] 	lmkwk=null;
        private byte[] 	kekwk=null;

    private byte[] pk=null;
    private byte[] signature=null;
    private byte[] pinBlockByZPK=null;
    private byte[] pinBlockByZPK2=null;

    private byte[] 	Lsdata=null;
    private byte[] 	icv=null;

        public EMResult()
        {

        }
        
       
    public byte[] getPk()
        {
                return pk;
        }

    public String getPkHexStr()
    {
            return UnionUtil.Bytes2HexString(pk);
    }

        public void setPk(byte[] bytVal)
        {
                pk=new byte[bytVal.length];
                System.arraycopy(bytVal, 0, pk, 0, bytVal.length);
        }

        public byte[] getSignature()
        {
                return signature;
        }
        public void setSignature(byte[] bytVal)
        {
                signature=new byte[bytVal.length];
                System.arraycopy(bytVal, 0, signature, 0, bytVal.length);
        }

        public byte[] getPinBlockByZPK2()
        {
                return pinBlockByZPK2;
        }
        public void setPinBlockByZPK2(byte[] bytVal)
        {
                pinBlockByZPK2=new byte[bytVal.length];
                System.arraycopy(bytVal, 0, pinBlockByZPK2, 0, bytVal.length);
        }

        public byte[] getPinBlockByZPK()
        {
                return pinBlockByZPK;
        }
        public void setPinBlockByZPK(byte[] bytVal)
        {
                pinBlockByZPK=new byte[bytVal.length];
                System.arraycopy(bytVal, 0, pinBlockByZPK, 0, bytVal.length);
        }

        public byte[] getData()
        {
                return data;
        }
        public void setData(byte[] bytVal)
        {
                data=new byte[bytVal.length];
                System.arraycopy(bytVal, 0, data, 0, bytVal.length);
        }

        public byte[] getLmkKek()
        {
                return lmkkek;
        }
        public void setLmkKek(byte[] bytVal)
        {
                lmkkek=new byte[bytVal.length];
                System.arraycopy(bytVal, 0, lmkkek, 0, bytVal.length);
        }

        public byte[] getKEK()
        {
                return kek;
        }
        public void setKEK(byte[] bytVal)
        {
                kek=new byte[bytVal.length];
                System.arraycopy(bytVal, 0, kek, 0, bytVal.length);
        }

        public byte[] getLmkWk()
        {
                return lmkwk;
        }
        public void setLmkWk(byte[] bytVal)
        {
                lmkwk=new byte[bytVal.length];
                System.arraycopy(bytVal, 0, lmkwk, 0, bytVal.length);
        }

        public byte[] getKekWk()
        {
                return kekwk;
        }
        public void setKekWk(byte[] bytVal)
        {
                kekwk=new byte[bytVal.length];
                System.arraycopy(bytVal, 0, kekwk, 0, bytVal.length);
        }

        public int getLen()
        {
                return length;
        }
        public void setLen(int bytVal)
        {
                length=bytVal;

        }

        public byte[] getCriperData()
        {
                return criperData;
        }
        public void setCriperData(byte[] bytVal)
        {
                criperData=new byte[bytVal.length];
                System.arraycopy(bytVal, 0, criperData, 0, bytVal.length);
        }
        public byte[] getMac()
        {
                return mac;
        }
        public void setMac(byte[] bytVal)
        {
                mac=new byte[bytVal.length];
                System.arraycopy(bytVal, 0, mac, 0, bytVal.length);
        }
        
        
        public int getRecode()
        {
                return recode;
        }
        public void setRecode(int i)
        {
                this.recode=i;
        }
        
      
        
        public byte[] getLsData()
        {
                return Lsdata;
        }

        public void setLsData(byte[] bytVal)
        {
                Lsdata=new byte[bytVal.length];
                System.arraycopy(bytVal, 0, Lsdata, 0, bytVal.length);
        }
        public byte[] getICV()
        {
                return icv;
        }
        public void setICV(byte[] bytVal)
        {
                icv=new byte[bytVal.length];
                System.arraycopy(bytVal, 0, icv, 0, bytVal.length);
        }
        public int getLsDataLen()
        {
                return Lsdata.length;
        }
        public void setLsDataLen(byte[] bytVal)
        {
                Lsdata=new byte[bytVal.length];
                System.arraycopy(bytVal, 0, Lsdata, 0, bytVal.length);
        }

}
