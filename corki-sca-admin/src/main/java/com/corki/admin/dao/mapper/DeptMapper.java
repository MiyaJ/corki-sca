package com.corki.admin.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.corki.admin.dao.entity.Dept;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 部门表 数据层
 *
 * @author Corki
 * @since 2024-12-09
 */
@Mapper
public interface DeptMapper extends BaseMapper<Dept> {

    /**
     * 查询部门管理数据
     *
     * @param dept 部门信息
     * @return 部门信息集合
     */
    List<Dept> selectDeptList(Dept dept);

    /**
     * 根据角色ID查询部门树信息
     *
     * @param roleId 角色ID
     * @return 选中部门列表
     */
    List<Long> selectDeptListByRoleId(Long roleId);

    /**
     * 根据ID查询所有子部门（正常状态）
     *
     * @param deptId 部门ID
     * @return 子部门数
     */
    int selectNormalChildrenDeptById(Long deptId);

    /**
     * 根据ID查询所有子部门
     *
     * @param deptId 部门ID
     * @return 子部门列表
     */
    List<Dept> selectChildrenDeptById(Long deptId);

    /**
     * 根据ID查询所有子部门（含正常状态和停用状态）
     *
     * @param deptId 部门ID
     * @return 子部门数
     */
    int selectAllChildrenDeptById(Long deptId);

    /**
     * 查询部门是否存在用户
     *
     * @param deptId 部门ID
     * @return 结果
     */
    int checkDeptExistUser(Long deptId);

    /**
     * 根据部门ID查询信息
     *
     * @param deptId 部门ID
     * @return 部门信息
     */
    Dept selectDeptById(Long deptId);

    /**
     * 根据ID查询信息
     *
     * @param id Id
     * @return 部门信息
     */
    Dept selectDeptByIdIncludingDeleted(Long id);

    /**
     * 根据ID查询子部门数量
     *
     * @param deptId 部门ID
     * @return 子部门数量
     */
    int selectDeptCountByParentId(Long deptId);

    /**
     * 校验部门名称是否唯一
     *
     * @param deptName 部门名称
     * @param parentId 父部门ID
     * @return 结果
     */
    Dept checkDeptNameUnique(@Param("deptName") String deptName, @Param("parentId") Long parentId);

    /**
     * 新增部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    int insertDept(Dept dept);

    /**
     * 修改部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    int updateDept(Dept dept);

    /**
     * 修改所在部门正常状态
     *
     * @param deptIds 部门ID组
     * @return 结果
     */
    int updateDeptStatusNormal(Long[] deptIds);

    /**
     * 删除部门管理信息
     *
     * @param deptId 部门ID
     * @return 结果
     */
    int deleteDeptById(Long deptId);
}