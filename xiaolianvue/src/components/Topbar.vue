<script setup lang="js">
import LineMdTiktok from '~icons/line-md/tiktok?width=32px&height=32px';
import LineMdGithubTwotone from '~icons/line-md/github-twotone?width=32px&height=32px';

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
                XiaolianShowerSense
                <div class="topbar_version">
                    v0.7
                </div>
            </div>
            <LineMdTiktok class="topbar_douyin" @click="jumpToDouyin" />
            <LineMdGithubTwotone class="topbar_github" @click="jumpToGithub" />
        </div>
    </div>

</template>

<style scoped>
@import "../font.css";
.topbar_bg {
    background-color: #03a9f4;
    border-radius: 0px 0px 10px 10px;
}
.topbar_version{
    font-size: small;
    padding-left: 2px;
}
.topbar_container {
    padding-block: 8px;
    align-items: center;
    padding-inline: 16px;
    display: flex;
    height: 40px;
}

.topbar_title {
    display: flex;
    font-family: 'Gilroy', sans-serif;
    font-weight: 800;
    font-size: 20px;
    color: whitesmoke;
}

.topbar_github {
    cursor: pointer;
    justify-self: end;
    margin-left: 9px;
}

.topbar_douyin {
    cursor: pointer;
    justify-self: end;
    margin-left: auto;
}
</style>