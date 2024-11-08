<script setup lang="js">
import { ElIcon, ElMessageBox } from 'element-plus';
import { h } from 'vue';
import LineMdCoffeeHalfEmptyTwotoneLoop from '~icons/line-md/coffee-half-empty-twotone-loop?width=28px&height=28px';

function click() {
    ElMessageBox({
        title: "推荐使用微信支付",
        message: h('img', {
            src: "http://47.96.24.132/sponsor.jpg",
            style: ""
        })
    })
}
</script>

<template>
    <div class="sponsor_bg">
        <LineMdCoffeeHalfEmptyTwotoneLoop class="sponsor_icon" />
        <div class="sponsor_container" @click="click">
            <div class="sponsor_title">
                此工具真的有帮助？
            </div>
            <div class="sponsor_text">
                给作者赞赏一杯奶茶吧。或者给两杯奶茶，怎么样？点击跳转😙
            </div>
        </div>
    </div>
</template>

<style>
.sponsor_icon {
    padding-right: 10px;
    padding-left: 7px;
    color: white;
    min-height: 28px;
    min-width: 28px;
}

.sponsor_bg {
    cursor: pointer;
    margin-block: 9px;
    background-color: #ea7293;
    display: flex;
    align-items: center;
    padding: 10px 7px;
    border-radius: 10px;
    font-weight: 400;

}

.sponsor_title {
    color: white;
    font-size: medium;
    font-weight: 600;
}

.sponsor_text {
    color: white;
    font-size: smaller;
    font-weight: 500;
}
</style>