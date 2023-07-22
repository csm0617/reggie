package com.csm.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.csm.reggie.common.BaseContext;
import com.csm.reggie.common.R;
import com.csm.reggie.entity.AddressBook;
import com.csm.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addressBook")
@Slf4j
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新增地址簿
     *
     * @param addressBook
     * @return
     */

    @PostMapping()
    public R<AddressBook> save(@RequestBody AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        //日志输出入参,看能否接收到
        log.info("addressBook：{}", addressBook);

        addressBookService.save(addressBook);
        return R.success(addressBook);

    }

    /**
     * 设置默认地址
     * 因为address_book表中有is_default字段用来标识默认地址
     * 所以在设置默认地址是应该把用户id包含的所有地址的字段is_default设置为0
     * 再将要需要设置为默认的地址的这个字段设置为1
     *
     * @param addressBook
     * @return
     */
    @Transactional
    @PutMapping("/default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook) {
        //日志输出入参,看能否接收到
        log.info("addressBook：{}", addressBook);

//        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        //注意!!!这里使用的是LambdaUpdateWrapper<>()
        LambdaUpdateWrapper<AddressBook> updateWrapper = new LambdaUpdateWrapper<>();
        Long userId = BaseContext.getCurrentId();
        //对应的sql: update address_book set is_default = 0 where user_id = ?
        updateWrapper.eq(userId != null, AddressBook::getUserId, userId);
        updateWrapper.set(AddressBook::getIsDefault, 0);
        addressBookService.update(updateWrapper);
        //设置为默认地址
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }

    /**
     * 根据id查询地址
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<AddressBook> get(@PathVariable Long id) {
        log.info("接收到的id为：{}", id);
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook != null) {
            return R.success(addressBook);
        } else {
            return R.error("没有找到该地址");
        }
    }


    /**
     * 查询指定用户的默认地址
     *
     * @return
     */
    @GetMapping
    public R<AddressBook> getDefault() {
        //构造条件构造器
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        //肯定查询的不是所有用户的默认地址，查询的是当前用户的默认地址
        Long userId = BaseContext.getCurrentId();
        queryWrapper.eq(userId != null, AddressBook::getUserId, userId);
        //再查询该用户is_default字段为1的地址
        queryWrapper.eq(AddressBook::getIsDefault, 1);
        //对应的sql为 select * from address_book where user_id = ? and is_default = 1
        AddressBook addressBook = addressBookService.getOne(queryWrapper);
        if (addressBook == null) {
            return R.error("没有找到该对象");
        }
        return R.success(addressBook);

    }

    /**
     * 查询指定用户的所有地址
     * @param addressBook
     * @return
     */

    @GetMapping("/list")
    public  R<List<AddressBook>> list(AddressBook addressBook){
        //获取当前用户的id,
        Long userId = BaseContext.getCurrentId();
        addressBook.setUserId(userId);
        log.info("addressBook: {}",addressBook);
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        //构造查询条件
        queryWrapper.eq(userId!=null,AddressBook::getUserId,userId);
        //查完以后根据更新时间进行排序
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);
        //查询
        List<AddressBook> addressBookList = addressBookService.list(queryWrapper);
        if (addressBookList!=null){
            return  R.success(addressBookList);
        }else {
            return R.error("该用户没有地址信息");
        }

    }


}
