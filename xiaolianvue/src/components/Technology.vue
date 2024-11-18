<script setup>
import LineMdCloudAltTagsTwotoneLoop from '~icons/line-md/cloud-alt-tags-twotone-loop?width=28px&height=28px';
function click() {
    window.open("https://beian.mps.gov.cn/#/query/webSearch")
}
</script>
<template>
    <div class="technology_bg">
        <LineMdCloudAltTagsTwotoneLoop class="technology_icon" />
        <div class="technology_container" @click="click">
            <div class="technology_title">
                XiaolianShowerSense 由 @海蓝色的咕咕鸽 (@aquamarine5) 开发并维护
            </div>
            <div class="technology_tips">
                本工具与智慧笑联无关。Copyright © 2024 AQ,RC., All rights reserved.<br />
                XiaolianShowerSense is an open-source project followed the LGPLv2.1 license.
            </div>
            <div class="technology_text">
                <img src="http://wash.aquamarine5.fun/netsafe.png" class="technology_img" />
                冀ICP备2024089369号
            </div>
        </div>
    </div>
</template>
<style>
.technology_img {
    height: 20px;
    width: 20px;
    padding-right: 4px;
}

.technology_tips {
    color: white;
    font-size: smaller;
}

.technology_icon {
    padding-right: 10px;
    padding-left: 7px;
    color: white;
    min-height: 28px;
    min-width: 28px;
}

.technology_bg {
    cursor: pointer;
    margin-block: 9px;
    background-color: #40a070cc;
    display: flex;
    align-items: center;
    padding: 10px 7px;
    border-radius: 10px;
    font-weight: 400;

}

.technology_title {
    color: white;
    font-size: medium;
    font-weight: 600;
}

.technology_text {
    font-family: sans-serif;
    padding-top: 2px;
    display: flex;
    color: white;
    font-size: smaller;
    font-weight: 500;
}
</style>