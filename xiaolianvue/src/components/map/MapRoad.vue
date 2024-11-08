<script setup>
import { ref, computed } from 'vue';
import MapWasher from './MapWasher.vue';


const props = defineProps(
    ["roadData",'isReady']
)
</script>

<template>
    <div class="road_container" v-if="isReady">
        <div class="road_neighbours">
            <MapWasher v-for="(washer, id) in props.roadData[0]" :key="id" :deviceInfo="washer"/>
        </div>
        <div class="road_display" :style="{ backgroundColor: roadDisplayColor }">

        </div>
        <div class="road_neighbours">
            <MapWasher v-for="(washer, id) in props.roadData[1]" :key="id" :deviceInfo="washer"/>
        </div>
    </div>
</template>

<script>
const roadDisplayValue = ref(0.5); // 假设这是你用来计算颜色的数值，范围在 0 到 1 之间

const startColor = { r: 0, g: 255, b: 0 }; // 起始颜色：绿色
const endColor = { r: 255, g: 0, b: 0 }; // 结束颜色：红色

const interpolateColor = (start, end, factor) => {
    const result = {
        r: Math.round(start.r + factor * (end.r - start.r)),
        g: Math.round(start.g + factor * (end.g - start.g)),
        b: Math.round(start.b + factor * (end.b - start.b))
    };
    return `rgb(${result.r}, ${result.g}, ${result.b})`;
};
const roadDisplayColor = computed(() => {
    return interpolateColor(startColor, endColor, roadDisplayValue.value);
});
</script>

<style>
.road_neighbours{
    display: flex;
    gap:5px;
}
.road_display{
    height: 5px;
}
</style>