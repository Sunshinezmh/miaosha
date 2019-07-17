package com.zmh.miaosha.dao;

import com.zmh.miaosha.dataobject.SequenceDo;

public interface SequenceDoMapper {
    int deleteByPrimaryKey(String name);

    int insert(SequenceDo record);

    int insertSelective(SequenceDo record);

    SequenceDo selectByPrimaryKey(String name);

    int updateByPrimaryKeySelective(SequenceDo record);

    int updateByPrimaryKey(SequenceDo record);

    SequenceDo getSequenceByName(String name);
}