<script setup>
const props=defineProps([
    "deviceInfo"
])
function formatColor(s) {
    if (s == 1) return '#A9A9A9'
    else if (s == 2) return '#00F'
    else return '#FF0'
}
</script>

<template>
    <div class="mapwasher_container" v-if="props.deviceInfo!=null">
        <div class="mapwasher_badge"  :style="{backgroundColor: formatColor(props.deviceInfo.status)}">

        </div>
    </div>
    <div class="mapwasher_container_blank" v-else>

    </div>
</template>

<style>

.mapwasher_badge{
    height: 3px;
    width: 3px;
}
.mapwasher_container_blank{
    height: 3px;
    width: 3px;
}
</style>