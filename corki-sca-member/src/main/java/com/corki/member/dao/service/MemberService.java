package com.corki.member.dao.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.corki.member.dao.entity.Member;
import com.corki.member.dao.mapper.MemberMapper;
@Service
public class MemberService extends ServiceImpl<MemberMapper, Member> {

}
