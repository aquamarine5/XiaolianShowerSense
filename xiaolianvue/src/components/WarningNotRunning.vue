<script setup lang="js">
import LineMdAlertLoop from '~icons/line-md/alert-loop?width=28px&height=28px';
var isrunning = defineModel('isrunning')
check()
setInterval(check, 60000)
function check() {
    var hour = new Date().getHours()
    isrunning.value = hour < 13 || hour > 23
}
</script>

<template>
    <div class="warning_notrunning_bg" v-if="isrunning">
        <LineMdAlertLoop class="warning_notrunning_icon"/>
        <div class="warning_notrunning_container">
            <div class="warning_notrunning_title">
                请注意！
            </div>
            <div class="warning_notrunning_text">
                浴室的服务时间在每天 13:30~22:30，现在并非浴室服务时间，故页面并不会实时更新。
            </div>
        </div>
    </div>
</template>

<style>
.warning_notrunning_icon {
    padding-right: 10px;
    padding-left: 7px;
}

.warning_notrunning_bg {
    margin-block: 9px;
    background-color: #f04b22;
    display: flex;
    align-items: center;
    padding: 10px 7px;
    border-radius: 10px;
    font-weight: 400;

}

.warning_notrunning_title {
    color: white;
    font-size: medium;
    font-weight: 600;
}

.warning_notrunning_text {
    color: white;
    font-size: smaller;
    font-weight: 500;
}
</style>