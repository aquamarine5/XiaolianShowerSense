<script setup lang="js">
import LineMdCompassTwotone from '~icons/line-md/compass-twotone?width=28px&height=28px';
import LineMdTiktok from '~icons/line-md/tiktok?width=32px&height=32px';
import LineMdGithubTwotone from '~icons/line-md/github-twotone?width=32px&height=32px';

const props = defineProps({
    washCount: {
        type: Number
    },
    washAvgTime: {
        type: String
    },
    useCount: {
        type: Number
    }
})

function jumpToGithub() {
    window.open("https://github.com/aquamarine5/XiaolianWebHelper", "_blanks")
}

function jumpToDouyin() {
    window.open("https://www.douyin.com/user/MS4wLjABAAAApuyqymIaQkpvKkbdH1X6W3A6XEgJl7kddGrZHxipJ7TbA1lCRaPJK5gZ1KX7pR1n", "_blanks")
}
</script>

<template>
    <div class="topbar_bg">
        <div class="topbar_container">
            <div class="topbar_title">
                XiaolianWebHelper
            </div>
            <LineMdTiktok class="topbar_douyin" @click="jumpToDouyin"/>
            <LineMdGithubTwotone class="topbar_github" @click="jumpToGithub"/>
        </div>
    </div>
    <div class="tipsbar_bg">
        <LineMdCompassTwotone class="tipsbar_icon"/>
        <div class="tipsbar_container">
            <div class="tipsbar_column">
                <div class="tipsbar_text">
                    当前统计洗浴次数：
                </div>
                <div class="tipsbar_value">
                    {{ props.washCount }}
                </div>
            </div>
            <div class="tipsbar_column">
                <div class="tipsbar_text">
                    平均洗浴时间：
                </div>
                <div class="tipsbar_value">
                    {{ props.washAvgTime }}
                </div>
            </div>
            <div class="tipsbar_column">
                <div class="tipsbar_text">
                    工具使用次数：
                </div>
                <div class="tipsbar_value">
                    {{ props.useCount }}
                </div>
            </div>
        </div>
    </div>
</template>

<style>
.tipsbar_column {
    display: flex;
}

.tipsbar_container {
    padding: 8px;
    padding-left: 20px;
    padding-bottom: 12px;
    font-size: small;
    font-weight: 600;
}

.tipsbar_icon {
    color: white;
    padding-left: 20px;
}

.tipsbar_bg {
    align-items: center;
    display: flex;
    background-color: #03a8f4a8;
    border-radius: 0px 0px 10px 10px;
}

.tipsbar_text {
    color: #b3e5fc;
}

.tipsbar_value {
    color: white;
    padding-left: 5px;
}

.topbar_bg {
    background-color: #03a9f4;
}

.topbar_container {
    padding-block: 8px;
    align-items: center;
    padding-inline: 20px;
    display: flex;
    height: 40px;
}

.topbar_title {
    font-family: 'Gilroy', sans-serif;
    font-weight: 800;
    font-size: 24px;
    color: whitesmoke;
}

.topbar_github {
    cursor: pointer;
    justify-self: end;
    margin-left: 9px;
}
.topbar_douyin{
    cursor: pointer;
    justify-self: end;
    margin-left: auto;
}
</style>