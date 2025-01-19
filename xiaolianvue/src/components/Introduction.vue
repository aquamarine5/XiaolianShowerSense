<script setup lang="js">
import LineMdEmojiSmileWinkTwotone from '~icons/line-md/emoji-smile-wink-twotone?width=28px&height=28px';
</script>

<template>
    <div class="introduction_bg">
        <LineMdEmojiSmileWinkTwotone class="introduction_icon" />
        <div class="introduction_container">
            <div class="introduction_title">
                这是什么？
            </div>
            <div class="introduction_text">
                XiaolianShowerSense 可以监控每个淋浴头的使用情况，并帮助分析如何最快找到没有人的淋浴头。
            </div>
            <!-- <div class="introduction_text" style="font-style: oblique; text-decoration: underline;">
                本工具还在持续更新中，呈现界面并非最终版本。
            </div> -->
        </div>
    </div>
</template>

<style>
.introduction_icon {
    padding-right: 8px;
    padding-left: 7px;
    color: white;
    min-height: 28px;
    min-width: 28px;
}

.introduction_bg {
    margin-block: 9px;
    background-color: #ffd000;
    display: flex;
    align-items: center;
    padding: 10px 7px;
    border-radius: 10px;
    font-weight: 400;

}

.introduction_title {
    color: white;
    font-size: medium;
    font-weight: 600;
}

.introduction_text {
    font-family: "Gilroy", sans-serif;
    color: white;
    font-size: smaller;
    font-weight: 500;
}
</style>