package com.van.deprecated.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by van on 2016/11/1.
 */
/*@Component("dateUtil")*/
public class DateUtil {
    private SimpleDateFormat sdf=new SimpleDateFormat("yyyy-mm-dd HH24Miss");

    public String format(Date date){
        return sdf.format(date);
    }

    public Date toDate(String datestr){
        try {
            return sdf.parse(datestr);
        } catch (ParseException e) {
            return null;
        }
    }
}
