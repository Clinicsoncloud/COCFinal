package com.abhaybmicoc.app.glucose.models;

import android.graphics.Bitmap;

/**
 * Created by Sreyas V Pariyath on 5/17/16.
 */
public class ResultsModel
    {
    String name,age,sex,date,operatorid,syncStatus,result,uuid,itemnumber,time,normalCount,mildCount,ModerateCount,severeCount,total,type,btadd;
    Bitmap thumbnail;
    int typeofan;
    boolean isPrime;
    public String getName()
        {
            return name;
        }

    public void setName(String name)
        {
            this.name = name;
        }

    public String getAge()
        {
            return age;
        }

    public void setAge(String age)
        {
            this.age = age;
        }

    public String getSex()
        {
            return sex;
        }

    public void setSex(String sex)
        {
            if(sex.equals("M"))
                {
                    this.sex = "Male";
                }
            else if(sex.equals("F"))
                {
                    this.sex = "Female";
                }
            else if(sex.equals("F(P)"))
                {
                    this.sex = "Female(Pregnant)";
                }
            else
                {
                    this.sex=sex;
                }

        }

    public String getDate()
        {
            return date;
        }

    public void setDate(String date)
        {
            this.date = date;
        }

    public String getOperatorid()
        {
            return operatorid;
        }

    public void setOperatorid(String operatorid)
        {
            this.operatorid = operatorid;
        }

    public String getSyncStatus()
        {
            return syncStatus;
        }

    public void setSyncStatus(String syncStatus)
        {
            this.syncStatus = syncStatus;
        }

    public String getResult()
        {
            return result;
        }

    public void setResult(String result)
        {
            this.result = result;
        }

    public String getUuid()
        {
            return uuid;
        }

    public void setUuid(String uuid)
        {
            this.uuid = uuid;
        }

    public String getItemnumber()
        {
            return itemnumber;
        }

    public void setItemnumber(String itemnumber)
        {
            this.itemnumber = itemnumber;
        }

    public String getTime()
        {
            return time;
        }

    public void setTime(String time)
        {
            this.time = time;
        }

    public Bitmap getThumbnail()
        {
            return thumbnail;
        }

    public void setThumbnail(Bitmap thumbnail)
        {
            this.thumbnail = thumbnail;
        }

    public int getTypeofan()
        {
            return typeofan;
        }

    public void setTypeofan(int typeofan)
        {
            this.typeofan = typeofan;
        }

    public String getNormalCount()
        {
            return normalCount;
        }

    public void setNormalCount(String normalCount)
        {
            this.normalCount = normalCount;
        }

    public String getMildCount()
        {
            return mildCount;
        }

    public void setMildCount(String mildCount)
        {
            this.mildCount = mildCount;
        }

    public String getModerateCount()
        {
            return ModerateCount;
        }

    public void setModerateCount(String moderateCount)
        {
            ModerateCount = moderateCount;
        }

    public String getSevereCount()
        {
            return severeCount;
        }

    public void setSevereCount(String severeCount)
        {
            this.severeCount = severeCount;
        }

    public String getTotal()
        {
            return total;
        }

    public void setTotal(String total)
        {
            this.total = total;
        }

    public String getType()
        {
            return type;
        }

    public void setType(String type)
        {
            this.type = type;
        }

    public boolean isPrime()
        {
            return isPrime;
        }

    public void setPrime(boolean prime)
        {
            isPrime = prime;
        }

    public String getBtadd()
        {
            return btadd;
        }

    public void setBtadd(String btadd)
        {
            this.btadd = btadd;
        }
    }
