<script setup>
const props=defineProps([
    "deviceInfo","avgWashTime"
])
function formatColor(device) {
    let s=device.status
    if(s==2&&props.avgWashTime/1000<(new Date().getTime()/1000-device.time/1000)) return '#F00'
    if (s == 1) return '#A9A9A9'
    else if (s == 2) return '#00F'
    else return '#FF0'
}
</script>

<template>
    <div class="mapwasher_container" v-if="props.deviceInfo!=null">
        <div class="mapwasher_badge"  :style="{backgroundColor: formatColor(props.deviceInfo)}">
            {{ props.deviceInfo.id }}
        </div>
    </div>
    <div class="mapwasher_badge" v-else>

    </div>
</template>

<style>

.mapwasher_badge{
    height: 10px;
    width: 10px;
    margin: 3px;
    font-size: small;
}
</style>