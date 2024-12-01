package app.backendservice.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateTimeUtil
{
    public static Date getNowDateTime()
    {
        Date inputDate = new Date();
        LocalDateTime ldt = LocalDateTime.ofInstant(inputDate.toInstant(), ZoneId.systemDefault());

        return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
    }
}
