package com.osondoson.backend.enums.season;

import com.osondoson.backend.common.exception.OsondosonException;
import com.osondoson.backend.enums.message.FailMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Season {
    SEASON_1415("2014/2015", 2014, 2015),
    SEASON_1516("2015/2016", 2015, 2016),
    SEASON_1617("2016/2017", 2016, 2017),
    SEASON_1718("2017/2018", 2017, 2018),
    SEASON_1819("2018/2019", 2018, 2019),
    SEASON_1920("2019/2020", 2019, 2020),
    SEASON_2021("2020/2021", 2020, 2021),
    SEASON_2122("2021/2022", 2021, 2022),
    SEASON_2223("2022/2023", 2022, 2023),
    SEASON_2324("2023/2024", 2023, 2024),
    SEASON_2425("2024/2025", 2024, 2025),
    SEASON_2526("2025/2026", 2025, 2026);

    private final String seasonName;
    private final int startYear;
    private final int endYear;

    public static String getSeasonNameByStartYear(Integer startYear) {
        return findByStartYear(startYear).getSeasonName();
    }

    private static Season findByStartYear(Integer startYear) {
        if (startYear == null) {
            throw new OsondosonException(FailMessage.SEASON_NOT_FOUND);
        }

        return Arrays.stream(values())
                .filter(season -> startYear.equals(season.getStartYear()))
                .findFirst()
                .orElseThrow(() -> new OsondosonException(FailMessage.SEASON_NOT_FOUND));
    }
}
