<script setup>
import { ref, computed } from 'vue';
import MapWasher from './MapWasher.vue';


const props = defineProps(
    ["roadData"]
)
var washerDetails = ref({
    left: ref([]),
    right: ref([])
})

</script>

<template>
    <div class="road_container">
        <div class="road_neighbours">
            <MapWasher v-for="(washer, id) in washerDetails.left" :status="washer.status"
                :lastUsedTime="washer.lastUsedTime" :displayid="id" :ref="'washer' + id" :key="id" />
        </div>
        <div class="road_display" :style="{ backgroundColor: roadDisplayColor}">

        </div>
        <div class="road_neighbours">
            <MapWasher v-for="(washer, id) in washerDetails.right" :status="washer.status"
                :lastUsedTime="washer.lastUsedTime" :displayid="id" :key="id" />
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
export default {
    methods: {
        setupRoad(data) {
            props.roadData[0].forEach(displayNo => {
                washerDetails.left.value[displayNo] = {
                    status: data.devices[displayNo].status,
                    lastUsedTime: data[displayNo].lastUsedTime
                }
            });
            props.roadData[1].forEach(displayNo => {
                washerDetails.right.value[displayNo] = {
                    status: data.devices[displayNo].status,
                    lastUsedTime: data.devices[displayNo].lastUsedTime
                }
            });
        },
        refreshRoad(data) {
            data.devices.forEach(id => {
                if (washerDetails.value.left[id]) {
                    washerDetails.value.left[id].status = data.devices[id].status
                    washerDetails.value.left[id].lastUsedTime = data.devices[id].lastUsedTime
                }
                if (washerDetails.value.right[id]) {
                    washerDetails.value.right[id].status = data.devices[id].status
                    washerDetails.value.right[id].lastUsedTime = data.devices[id].lastUsedTime
                }
            })
        }
    }
}
</script>