<script setup>
import LineMdCompassTwotone from '~icons/line-md/compass-twotone?width=28px&height=28px';
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

</script>

<template>
    <div class="tipsbar_bg">
        <LineMdCompassTwotone class="tipsbar_icon" />
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

<style lang="css" scoped>
.tipsbar_column {
    display: flex;
}

.tipsbar_container {
    font-size: small;
    font-weight: 600;
}

.tipsbar_icon {
    color: white;
    padding-right: 10px;
    padding-left: 7px;
    min-height: 28px;
    min-width: 28px;
}

.tipsbar_bg {
    margin-block: 9px;
    padding: 10px 7px;
    align-items: center;
    display: flex;
    background-color: #03a8f4a8;
    border-radius: 10px;
}

.tipsbar_text {
    color: #b3e5fc;
}

.tipsbar_value {
    color: white;
}
</style>