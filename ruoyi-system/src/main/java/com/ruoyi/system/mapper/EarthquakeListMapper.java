package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.EarthquakeList;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EarthquakeListMapper extends BaseMapper<EarthquakeList> {
    /*
     * 增
     * */
    int addEq(EarthquakeList eq);
    /*
     * 删
     * */
    int deleteEq(String eqid);
    /*
     * 改
     * */
    int updateEq(EarthquakeList eq);
    /*
     * 查
     * */
    List<EarthquakeList> selectAllEq();
    // 4.5级及以上地震数据
    List<EarthquakeList> selectKeyEq();
    // 最新地震数据
    List<EarthquakeList> selectLatestEq();

    //导入表实用
    @Select("SELECT * FROM earthquakeList WHERE position LIKE CONCAT('%', #{position}, '%') " +
            "AND magnitude = #{magnitude} " +
            "AND TO_CHAR(time, 'YYYY-MM-DD HH24:MI:SS') = #{time}")
    List<EarthquakeList> findEarthquakeIdByTimeAndPosition(@Param("time") String time, @Param("position") String position,
                                                           @Param("magnitude") String magnitude);


}
